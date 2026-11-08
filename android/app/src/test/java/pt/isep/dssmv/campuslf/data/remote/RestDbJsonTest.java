package pt.isep.dssmv.campuslf.data.remote;

import com.google.gson.Gson;
import org.junit.Test;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;
import static org.junit.Assert.*;

public final class RestDbJsonTest {
    private final Gson gson = RestDbJson.create();

    @Test public void readsEmbeddedLookupRecords() {
        String json = "{\"_id\":\"item1\",\"title\":\"Lost keys\","
                + "\"building\":[{\"_id\":\"b1\",\"name\":\"Building F\"}],"
                + "\"category\":[{\"_id\":\"c1\",\"name\":\"Keys\"}],"
                + "\"photos\":[\"photo1\"]}";
        RemoteFoundItem item = gson.fromJson(json, RemoteFoundItem.class);
        assertEquals("Building F", item.getBuildingName());
        assertEquals("Keys", item.getCategoryName());
        assertEquals("photo1", item.getFirstPhotoId());
    }

    @Test public void acceptsLookupIdentifiersWithoutEmbeddedObjects() {
        String json = "{\"_id\":\"item2\",\"building\":[\"b1\"],"
                + "\"category\":[\"c1\"]}";
        RemoteFoundItem item = gson.fromJson(json, RemoteFoundItem.class);
        assertEquals("b1", item.getBuildingId());
        assertEquals("c1", item.getCategoryId());
    }

    @Test public void readsInlinePhotoAlongsideOptionalGps() {
        RemoteFoundItem item = gson.fromJson("{\"_id\":\"item3\",\"photo_base64\":\"encoded-jpeg\","
                + "\"latitude\":41.178,\"longitude\":-8.608}", RemoteFoundItem.class);
        assertEquals("encoded-jpeg", item.getPhotoBase64());
        assertTrue(item.getPhotos().isEmpty());
        assertEquals(41.178, item.getLatitude(), 0.000001);
        assertEquals(-8.608, item.getLongitude(), 0.000001);
    }
}
