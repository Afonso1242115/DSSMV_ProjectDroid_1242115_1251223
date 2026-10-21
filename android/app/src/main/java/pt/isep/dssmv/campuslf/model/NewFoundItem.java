package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/** JSON sent to POST /rest/founditems. */
public final class NewFoundItem {
    private final String title;
    private final String description;
    @SerializedName("detailed_location") private final String detailedLocation;
    @SerializedName("found_date") private final String foundDate;
    private final List<String> building;
    private final List<String> category;
    private final List<String> photos;
    private final Double latitude;
    private final Double longitude;

    public NewFoundItem(String title, String description, String detailedLocation,
                        String foundDate, String buildingId, String categoryId,
                        String photoId, Double latitude, Double longitude) {
        this.title = title;
        this.description = description;
        this.detailedLocation = detailedLocation;
        this.foundDate = foundDate;
        this.building = Collections.singletonList(buildingId);
        this.category = Collections.singletonList(categoryId);
        this.photos = Collections.singletonList(photoId);
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
