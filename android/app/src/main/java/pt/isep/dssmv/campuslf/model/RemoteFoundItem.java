package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

/** RESTDB Lookup and Image fields are always represented by arrays. */
public final class RemoteFoundItem {
    @SerializedName("_id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("detailed_location") private String detailedLocation;
    @SerializedName("found_date") private String foundDate;
    @SerializedName("building") private List<Building> building;
    @SerializedName("category") private List<Category> category;
    @SerializedName("photos") private List<String> photos;
    @SerializedName("latitude") private Double latitude;
    @SerializedName("longitude") private Double longitude;

    public String getId() { return id; }
    public String getTitle() { return title == null ? "" : title; }
    public String getDescription() { return description == null ? "" : description; }
    public String getDetailedLocation() { return detailedLocation == null ? "" : detailedLocation; }
    public String getFoundDate() { return foundDate == null ? "" : foundDate; }
    public List<Building> getBuilding() { return building == null ? Collections.emptyList() : building; }
    public List<Category> getCategory() { return category == null ? Collections.emptyList() : category; }
    public List<String> getPhotos() { return photos == null ? Collections.emptyList() : photos; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getBuildingName() { return getBuilding().isEmpty() ? "Não indicado" : getBuilding().get(0).getName(); }
    public String getCategoryName() { return getCategory().isEmpty() ? "Não indicada" : getCategory().get(0).getName(); }
    public String getBuildingId() { return getBuilding().isEmpty() ? null : getBuilding().get(0).getId(); }
    public String getCategoryId() { return getCategory().isEmpty() ? null : getCategory().get(0).getId(); }
    public String getFirstPhotoId() { return getPhotos().isEmpty() ? null : getPhotos().get(0); }
    public RemoteFoundItem() { }
    public void setId(String value) { id = value; }
    public void setTitle(String value) { title = value; }
    public void setDescription(String value) { description = value; }
    public void setDetailedLocation(String value) { detailedLocation = value; }
    public void setFoundDate(String value) { foundDate = value; }
    public void setBuilding(List<Building> value) { building = value; }
    public void setCategory(List<Category> value) { category = value; }
    public void setPhotos(List<String> value) { photos = value; }
    public void setLatitude(Double value) { latitude = value; }
    public void setLongitude(Double value) { longitude = value; }
}
