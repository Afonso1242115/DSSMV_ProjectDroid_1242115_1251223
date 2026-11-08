package pt.isep.dssmv.campuslf.data.remote;

import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.junit.Assert.*;

public final class RestDbRoutingTest {
    @Test public void mediaHostDoesNotRedirectCollectionRequests() throws Exception {
        try (MockWebServer collections = new MockWebServer(); MockWebServer media = new MockWebServer()) {
            collections.start();
            media.start();
            RestDbService api = new Retrofit.Builder().baseUrl(collections.url("/"))
                    .addConverterFactory(GsonConverterFactory.create(RestDbJson.create()))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
                    .build().create(RestDbService.class);
            collections.enqueue(new MockResponse().setBody("[]"));
            api.getBuildings(1000).blockingGet();
            RecordedRequest buildings = collections.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(buildings);
            assertEquals("GET", buildings.getMethod());
            assertEquals("/rest/buildings?max=1000", buildings.getPath());

            media.enqueue(new MockResponse().setBody("{\"msg\":\"OK\",\"ids\":[\"photo1\"]}"));
            MultipartBody.Part photo = MultipartBody.Part.createFormData("myfile", "found-item.jpg",
                    RequestBody.create(MediaType.parse("image/jpeg"), new byte[]{1, 2, 3}));
            assertEquals("photo1", api.uploadPhoto(media.url("/media").toString(), photo)
                    .blockingGet().getFirstId());
            RecordedRequest upload = media.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(upload);
            assertEquals("POST", upload.getMethod());
            assertEquals("/media", upload.getPath());
            assertTrue(upload.getHeader("Content-Type").startsWith("multipart/form-data;"));

            collections.enqueue(new MockResponse().setBody("[]"));
            api.getCategories(1000).blockingGet();
            RecordedRequest categories = collections.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(categories);
            assertEquals("/rest/categories?max=1000", categories.getPath());
            assertEquals(2, collections.getRequestCount());
            assertEquals(1, media.getRequestCount());
        }
    }
}
