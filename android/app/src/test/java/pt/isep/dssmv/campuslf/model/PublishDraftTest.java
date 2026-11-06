package pt.isep.dssmv.campuslf.model;

import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public final class PublishDraftTest {
    private static PublishDraft draft(String title, String date, String photoPath) {
        return new PublishDraft(title, "Found on a study table", "Room F201", date,
                "building-f", "electronics", photoPath, null, null);
    }

    @Test public void trimsFieldsAndCreatesLookupLists() {
        PublishDraft value = draft("  Blue headphones  ", LocalDate.now().toString(), "/tmp/photo.jpg");
        assertEquals("Blue headphones", value.title);
        NewFoundItem posted = value.withPhotoId("media-1");
        assertEquals("building-f", posted.getBuilding().get(0));
        assertEquals("electronics", posted.getCategory().get(0));
        assertEquals("media-1", posted.getPhotos().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsFutureFoundDate() {
        draft("Blue headphones", LocalDate.now().plusDays(1).toString(), "/tmp/photo.jpg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingPhotoPath() {
        draft("Blue headphones", LocalDate.now().toString(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsOneSidedGpsCoordinates() {
        new PublishDraft("Blue headphones", "Found on the table", "Room F201",
                LocalDate.now().toString(), "building-f", "electronics", "/tmp/photo.jpg",
                41.0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCoordinatesOutsideRange() {
        new PublishDraft("Blue headphones", "Found on the table", "Room F201",
                LocalDate.now().toString(), "building-f", "electronics", "/tmp/photo.jpg",
                91.0, -8.0);
    }
}
