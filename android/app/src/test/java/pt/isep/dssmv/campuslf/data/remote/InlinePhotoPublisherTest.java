package pt.isep.dssmv.campuslf.data.remote;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.isep.dssmv.campuslf.model.PhotoLimits;
import pt.isep.dssmv.campuslf.model.PublishDraft;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static org.junit.Assert.*;

public final class InlinePhotoPublisherTest {
    private MockWebServer server;
    private RestDbService api;
    private final byte[] photo = new byte[]{(byte) 0xff, (byte) 0xd8, 1, 2, 3, (byte) 0xff, (byte) 0xd9};

    @Before public void openServer() throws IOException {
        server = new MockWebServer();
        server.start();
        api = new Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create(RestDbJson.create()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
                .build().create(RestDbService.class);
    }

    @After public void closeServer() throws IOException { server.shutdown(); }

    @Test public void postsPhotoLookupsAndGpsInOneCollectionRequest() throws Exception {
        enqueuePhoto(photo);
        InlinePhotoPublisher.Receipt receipt = InlinePhotoPublisher.publish(api, draft(), photo).blockingGet();
        assertTrue(receipt.photoConfirmed);
        assertEquals("item1", receipt.item.getId());
        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull(request);
        assertEquals("POST", request.getMethod());
        assertEquals("/rest/founditems", request.getPath());
        assertTrue(request.getHeader("Content-Type").startsWith("application/json"));
        JsonObject json = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
        assertArrayEquals(photo, Base64.getDecoder().decode(json.get("photo_base64").getAsString()));
        assertEquals("b1", json.getAsJsonArray("building").get(0).getAsString());
        assertEquals("c1", json.getAsJsonArray("category").get(0).getAsString());
        assertEquals(0, json.getAsJsonArray("photos").size());
        assertEquals(41.178, json.get("latitude").getAsDouble(), 0.000001);
        assertEquals(-8.608, json.get("longitude").getAsDouble(), 0.000001);
        assertEquals(1, server.getRequestCount());
    }

    @Test public void keepsSixtyKilobytePhotoAndLongUnicodeDetailsBelowBudget() throws Exception {
        byte[] large = new byte[PhotoLimits.MAX_JPEG_BYTES];
        Arrays.fill(large, (byte) 123);
        enqueuePhoto(large);
        PublishDraft draft = new PublishDraft("\u00e1".repeat(80), "\u6f22".repeat(500),
                "\u00e9".repeat(120), LocalDate.now().toString(), "b1", "c1", "/private/photo.jpg", null, null);
        assertTrue(InlinePhotoPublisher.publish(api, draft, large).blockingGet().photoConfirmed);
        String json = server.takeRequest(1, TimeUnit.SECONDS).getBody().readUtf8();
        assertTrue(json.getBytes(StandardCharsets.UTF_8).length <= PhotoLimits.MAX_DOCUMENT_BYTES);
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        assertEquals(80_000, object.get("photo_base64").getAsString().length());
        assertArrayEquals(large, Base64.getDecoder().decode(object.get("photo_base64").getAsString()));
        assertFalse(object.has("latitude"));
        assertFalse(object.has("longitude"));
    }

    @Test public void rejectsOversizedPhotoBeforeSending() {
        InlinePhotoPublisher.publish(api, draft(), new byte[PhotoLimits.MAX_JPEG_BYTES + 1])
                .test().assertError(IOException.class);
        assertEquals(0, server.getRequestCount());
    }

    @Test public void rejectsOversizedJsonBeforeSending() {
        PublishDraft draft = new PublishDraft("Found keys", "Found on a table", "Room F201",
                LocalDate.now().toString(), "b".repeat(15_000), "c1", "/private/photo.jpg", null, null);
        InlinePhotoPublisher.publish(api, draft, new byte[PhotoLimits.MAX_JPEG_BYTES])
                .test().assertError(error -> error instanceof IOException && error.getMessage().contains("too large"));
        assertEquals(0, server.getRequestCount());
    }

    @Test public void readsBackPhotoWhenPostResponseOmitsIt() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
        enqueuePhoto(photo);
        assertTrue(InlinePhotoPublisher.publish(api, draft(), photo).blockingGet().photoConfirmed);
        assertEquals("POST", server.takeRequest(1, TimeUnit.SECONDS).getMethod());
        RecordedRequest verification = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals("GET", verification.getMethod());
        assertEquals("/rest/founditems/item1", verification.getPath());
        assertEquals(2, server.getRequestCount());
    }

    @Test public void doesNotRetryAcceptedPostWhenPhotoFieldIsMissing() {
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
        InlinePhotoPublisher.Receipt receipt = InlinePhotoPublisher.publish(api, draft(), photo).blockingGet();
        assertEquals("item1", receipt.item.getId());
        assertFalse(receipt.photoConfirmed);
        assertEquals(2, server.getRequestCount());
    }

    @Test public void doesNotRetryAcceptedPostWhenVerificationFails() {
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
        server.enqueue(new MockResponse().setResponseCode(500));
        InlinePhotoPublisher.Receipt receipt = InlinePhotoPublisher.publish(api, draft(), photo).blockingGet();
        assertEquals("item1", receipt.item.getId());
        assertFalse(receipt.photoConfirmed);
        assertEquals(2, server.getRequestCount());
    }

    @Test public void keepsCreatedIdIfVerificationReturnsAnotherRecord() {
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\"}"));
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item2\",\"photo_base64\":\""
                + Base64.getEncoder().encodeToString(photo) + "\"}"));
        InlinePhotoPublisher.Receipt receipt = InlinePhotoPublisher.publish(api, draft(), photo).blockingGet();
        assertEquals("item1", receipt.item.getId());
        assertFalse(receipt.photoConfirmed);
    }

    @Test public void reportsRejectedCollectionRouteAndDoesNotRetry() {
        server.enqueue(new MockResponse().setResponseCode(403).setBody("Collection POST denied"));
        InlinePhotoPublisher.publish(api, draft(), photo).test().assertError(error ->
                error instanceof IOException && error.getMessage().contains("/rest/founditems")
                        && error.getMessage().contains("Collection POST denied"));
        assertEquals(1, server.getRequestCount());
    }

    private void enqueuePhoto(byte[] bytes) {
        server.enqueue(new MockResponse().setBody("{\"_id\":\"item1\",\"photo_base64\":\""
                + Base64.getEncoder().encodeToString(bytes) + "\"}"));
    }

    private static PublishDraft draft() {
        return new PublishDraft("Found keys", "Found on a table", "Room F201",
                LocalDate.now().toString(), "b1", "c1", "/private/photo.jpg", 41.178, -8.608);
    }
}
