package pt.isep.dssmv.campuslf.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import pt.isep.dssmv.campuslf.CampusLfApplication;
import pt.isep.dssmv.campuslf.data.CampusRepository;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.model.PublishDraft;
import pt.isep.dssmv.campuslf.model.PublishResult;
import pt.isep.dssmv.campuslf.util.ErrorMessages;

public final class AddItemViewModel extends AndroidViewModel {
    private final CampusRepository repository;
    private final SavedStateHandle state;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<OperationState> references = new MutableLiveData<>(OperationState.idle(""));
    private final MutableLiveData<OperationState> publication = new MutableLiveData<>(OperationState.idle(""));
    private final MutableLiveData<PublishResult> published = new MutableLiveData<>();

    public AddItemViewModel(@NonNull Application application, SavedStateHandle state) {
        super(application);
        this.state = state;
        repository = ((CampusLfApplication) application).getCampusRepository();
        if (!state.contains("found_date")) state.set("found_date", LocalDate.now().toString());
        refreshReferences();
    }

    public LiveData<List<BuildingEntity>> getBuildings() { return repository.observeBuildings(); }
    public LiveData<List<CategoryEntity>> getCategories() { return repository.observeCategories(); }
    public LiveData<OperationState> getReferences() { return references; }
    public LiveData<OperationState> getPublication() { return publication; }
    public LiveData<PublishResult> getPublished() { return published; }
    public LiveData<String> getPhoto() { return state.getLiveData("photo_path"); }
    public LiveData<String> getDate() { return state.getLiveData("found_date"); }
    public LiveData<String> getLocationStatus() {
        return state.getLiveData("location_status", "GPS is optional. A written location is required.");
    }
    public void locationMessage(String message) { state.set("location_status", message); }
    public void setCoordinates(double latitude, double longitude) {
        state.set("latitude", latitude);
        state.set("longitude", longitude);
        locationMessage(String.format(Locale.US, "GPS: %.6f, %.6f", latitude, longitude));
    }
    public void clearCoordinates() {
        state.remove("latitude");
        state.remove("longitude");
        locationMessage("No GPS coordinates. You can publish using the written location.");
    }
    public String getText(String key) { String value = state.get(key); return value == null ? "" : value; }
    public void setText(String key, String value) { state.set(key, value); }
    public boolean isPublishing() {
        OperationState current = publication.getValue();
        return current != null && current.isRunning();
    }

    public void refreshReferences() {
        OperationState current = references.getValue();
        if ((current != null && current.isRunning()) || isPublishing()) return;
        references.setValue(OperationState.running("Loading buildings and categories..."));
        disposables.add(repository.refreshReferences().observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> references.setValue(OperationState.success("Buildings and categories updated.")),
                        error -> references.setValue(OperationState.error(ErrorMessages.describe(error)))));
    }

    public void photoResult(boolean success) {
        String path = getText("pending_photo");
        state.remove("pending_photo");
        if (path.isEmpty()) return;
        File file = new File(path);
        if (success && file.isFile() && file.length() > 0) {
            String previous = getText("photo_path");
            state.set("photo_path", path);
            if (!previous.isEmpty() && !previous.equals(path)) new File(previous).delete();
        } else file.delete();
    }

    public void publish() {
        if (isPublishing() || published.getValue() != null) return;
        final PublishDraft draft;
        try {
            draft = new PublishDraft(getText("title"), getText("description"), getText("location"),
                    getText("found_date"), getText("building_id"), getText("category_id"),
                    getText("photo_path"), state.get("latitude"), state.get("longitude"));
        } catch (IllegalArgumentException error) {
            publication.setValue(OperationState.error(error.getMessage()));
            return;
        }
        publication.setValue(OperationState.running("Uploading photograph and publishing item..."));
        disposables.add(repository.publish(draft).observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    publication.setValue(OperationState.success("Item published."));
                    published.setValue(result);
                }, error -> publication.setValue(OperationState.error(ErrorMessages.describe(error)))));
    }

    @Override protected void onCleared() { disposables.clear(); }
}
