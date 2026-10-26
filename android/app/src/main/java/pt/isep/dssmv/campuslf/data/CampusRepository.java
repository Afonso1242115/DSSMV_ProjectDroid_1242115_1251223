package pt.isep.dssmv.campuslf.data;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.isep.dssmv.campuslf.data.local.CampusDao;
import pt.isep.dssmv.campuslf.data.local.CampusDatabase;
import pt.isep.dssmv.campuslf.data.local.ItemMapper;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.data.remote.RestDbClient;
import pt.isep.dssmv.campuslf.data.remote.RestDbService;
import pt.isep.dssmv.campuslf.model.PublishDraft;
import pt.isep.dssmv.campuslf.model.PublishResult;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;
import pt.isep.dssmv.campuslf.util.PhotoUtils;
import retrofit2.HttpException;

/** Coordinates the remote API and local cache. Activities never access either source directly. */
public final class CampusRepository {
    private static final int PAGE_SIZE = 100;
    private static volatile CampusRepository instance;
    private final CampusDao dao;
    private final RestDbService api;
    private final Scheduler dataScheduler;
    private Single<Integer> currentRefresh;

    private CampusRepository(Context context) {
        dao = CampusDatabase.getInstance(context).campusDao();
        api = RestDbClient.service();
        // Serial IO prevents a stale refresh snapshot from overwriting a just-published item.
        dataScheduler = Schedulers.from(Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "campuslf-data");
            thread.setDaemon(true);
            return thread;
        }));
    }

    public static CampusRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (CampusRepository.class) {
                if (instance == null) instance = new CampusRepository(context.getApplicationContext());
            }
        }
        return instance;
    }

    public LiveData<List<FoundItemEntity>> observeItems(String buildingId, String categoryId) {
        return dao.observeItems(buildingId, categoryId);
    }

    public LiveData<FoundItemEntity> observeItem(String id) { return dao.observeItem(id); }
    public LiveData<List<BuildingEntity>> observeBuildings() { return dao.observeBuildings(); }
    public LiveData<List<CategoryEntity>> observeCategories() { return dao.observeCategories(); }

    /** Room remains the source observed by the UI, even after a network refresh. */
    public synchronized Single<Integer> refresh() {
        if (currentRefresh == null) {
            currentRefresh = api.getBuildings(1000).flatMap(buildings ->
                            api.getCategories(1000).flatMap(categories ->
                                    fetchPages(0, new ArrayList<>()).map(items -> {
                                        List<BuildingEntity> localBuildings = ItemMapper.buildings(buildings);
                                        List<CategoryEntity> localCategories = ItemMapper.categories(categories);
                                        List<FoundItemEntity> localItems = ItemMapper.items(items,
                                                localBuildings, localCategories);
                                        dao.replaceSnapshot(localItems, localBuildings, localCategories);
                                        return localItems.size();
                                    })))
                    .subscribeOn(dataScheduler)
                    .doFinally(() -> {
                        synchronized (CampusRepository.this) { currentRefresh = null; }
                    }).cache();
        }
        return currentRefresh;
    }

    private Single<List<RemoteFoundItem>> fetchPages(int skip, List<RemoteFoundItem> all) {
        if (skip >= 100_000) return Single.error(new IOException("The server returned too many pages."));
        return api.getItems(PAGE_SIZE, skip, "_id", 1).flatMap(page -> {
            all.addAll(page);
            return page.size() < PAGE_SIZE ? Single.just(all)
                    : fetchPages(skip + PAGE_SIZE, all);
        });
    }

    public Completable refreshReferences() {
        return api.getBuildings(1000).flatMap(buildings ->
                api.getCategories(1000).map(categories -> {
                    dao.replaceReferences(ItemMapper.buildings(buildings), ItemMapper.categories(categories));
                    return true;
                })).ignoreElement().subscribeOn(dataScheduler);
    }

    public Completable refreshItem(String id) {
        return api.getItem(id).doOnSuccess(item -> dao.upsertItem(ItemMapper.item(item,
                        dao.readBuildings(), dao.readCategories())))
                .ignoreElement().onErrorResumeNext(error -> {
                    if (error instanceof HttpException && ((HttpException) error).code() == 404) {
                        return Completable.fromAction(() -> dao.deleteItem(id))
                                .andThen(Completable.error(error));
                    }
                    return Completable.error(error);
                }).subscribeOn(dataScheduler);
    }

    /** Compress -> upload media -> publish document -> update Room, all on the data worker. */
    public Single<PublishResult> publish(PublishDraft draft) {
        return Single.fromCallable(() -> PhotoUtils.compressedJpeg(new File(draft.photoPath)))
                .map(bytes -> MultipartBody.Part.createFormData("myfile", "found-item.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), bytes)))
                .flatMap(api::uploadPhoto)
                .flatMap(upload -> {
                    String photoId = upload.getFirstId();
                    if (photoId == null || photoId.isEmpty()) {
                        return Single.error(new IOException("Photo upload returned no identifier."));
                    }
                    return api.createItem(draft.withPhotoId(photoId)).map(created -> {
                        if (created.getId() == null || created.getId().isEmpty()) {
                            throw new IOException("The server response contained no record ID.");
                        }
                        boolean cached = true;
                        try {
                            String buildingName = "Not specified";
                            for (BuildingEntity building : dao.readBuildings())
                                if (building.id.equals(draft.buildingId)) buildingName = building.name;
                            String categoryName = "Not specified";
                            for (CategoryEntity category : dao.readCategories())
                                if (category.id.equals(draft.categoryId)) categoryName = category.name;
                            dao.upsertItem(new FoundItemEntity(created.getId(), draft.title,
                                    draft.description, draft.detailedLocation, draft.foundDate,
                                    draft.buildingId, buildingName, draft.categoryId, categoryName,
                                    photoId, draft.latitude, draft.longitude));
                        } catch (RuntimeException cacheError) {
                            // A server-confirmed publication must never be reported as a failed POST.
                            cached = false;
                        }
                        return new PublishResult(created.getId(), cached);
                    });
                }).subscribeOn(dataScheduler);
    }
}
