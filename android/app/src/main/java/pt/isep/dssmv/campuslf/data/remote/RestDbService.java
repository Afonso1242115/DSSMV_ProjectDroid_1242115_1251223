package pt.isep.dssmv.campuslf.data.remote;

import java.util.List;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import pt.isep.dssmv.campuslf.model.Building;
import pt.isep.dssmv.campuslf.model.Category;
import pt.isep.dssmv.campuslf.model.MediaUploadResponse;
import pt.isep.dssmv.campuslf.model.NewFoundItem;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestDbService {
    @GET("rest/buildings")
    Single<List<Building>> getBuildings(@Query("max") int max);

    @GET("rest/categories")
    Single<List<Category>> getCategories(@Query("max") int max);

    @GET("rest/founditems")
    Single<List<RemoteFoundItem>> getItems(@Query("max") int max, @Query("skip") int skip,
                                           @Query("sort") String sort, @Query("dir") int direction);

    @GET("rest/founditems/{id}")
    Single<RemoteFoundItem> getItem(@Path("id") String id);

    @POST("rest/founditems")
    Single<RemoteFoundItem> createItem(@Body NewFoundItem item);

    @Multipart @POST("media")
    Single<MediaUploadResponse> uploadPhoto(@Part MultipartBody.Part file);
}
