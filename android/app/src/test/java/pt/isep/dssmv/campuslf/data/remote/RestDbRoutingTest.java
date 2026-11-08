package pt.isep.dssmv.campuslf.data.remote;

import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.junit.Assert.*;

public final class RestDbRoutingTest {
    @Test public void collectionRequestsUseExpectedApiPaths() throws Exception {
        try (MockWebServer collections = new MockWebServer()) {
            collections.start();
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

            collections.enqueue(new MockResponse().setBody("[]"));
            api.getCategories(1000).blockingGet();
            RecordedRequest categories = collections.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(categories);
            assertEquals("/rest/categories?max=1000", categories.getPath());

            collections.enqueue(new MockResponse().setBody("[]"));
            api.getItems(100, 100, "_id", 1).blockingGet();
            RecordedRequest page = collections.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(page);
            assertEquals("/rest/founditems?max=100&skip=100&sort=_id&dir=1", page.getPath());

            collections.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
            assertEquals("item1", api.getItem("item1").blockingGet().getId());
            RecordedRequest details = collections.takeRequest(1, TimeUnit.SECONDS);
            assertNotNull(details);
            assertEquals("/rest/founditems/item1", details.getPath());
            assertEquals(4, collections.getRequestCount());
        }
    }
}
