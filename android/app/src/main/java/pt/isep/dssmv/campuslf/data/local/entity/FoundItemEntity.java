package pt.isep.dssmv.campuslf.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Local cache row. The primary key is the server _id, preventing duplicate cached items. */
@Entity(tableName = "found_items_cache", indices = {@Index("buildingId"), @Index("categoryId")})
public final class FoundItemEntity {
    @PrimaryKey @NonNull public final String id;
    @NonNull public final String title;
    @NonNull public final String description;
    @NonNull public final String detailedLocation;
    @NonNull public final String foundDate;
    @NonNull public final String buildingId;
    @NonNull public final String buildingName;
    @NonNull public final String categoryId;
    @NonNull public final String categoryName;
    public final String photoId;
    public final Double latitude;
    public final Double longitude;

    public FoundItemEntity(@NonNull String id, @NonNull String title,
                           @NonNull String description, @NonNull String detailedLocation,
                           @NonNull String foundDate, @NonNull String buildingId,
                           @NonNull String buildingName, @NonNull String categoryId,
                           @NonNull String categoryName, String photoId,
                           Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.detailedLocation = detailedLocation;
        this.foundDate = foundDate;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.photoId = photoId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
