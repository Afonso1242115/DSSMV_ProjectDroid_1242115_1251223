package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/** JSON sent to POST /rest/founditems. */
public final class NewFoundItem {
    private String title;
    private String description;
    @SerializedName("detailed_location") private String detailedLocation;
    @SerializedName("found_date") private String foundDate;
    private List<String> building;
    private List<String> category;
    private List<String> photos;
    private Double latitude;
    private Double longitude;

    public NewFoundItem() { }

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
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDetailedLocation() { return detailedLocation; }
    public String getFoundDate() { return foundDate; }
    public List<String> getBuilding() { return building; }
    public List<String> getCategory() { return category; }
    public List<String> getPhotos() { return photos; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public void setTitle(String value) { title = value; }
    public void setDescription(String value) { description = value; }
    public void setDetailedLocation(String value) { detailedLocation = value; }
    public void setFoundDate(String value) { foundDate = value; }
    public void setBuilding(List<String> value) { building = value; }
    public void setCategory(List<String> value) { category = value; }
    public void setPhotos(List<String> value) { photos = value; }
    public void setLatitude(Double value) { latitude = value; }
    public void setLongitude(Double value) { longitude = value; }
}
