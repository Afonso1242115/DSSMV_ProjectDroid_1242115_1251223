package pt.isep.dssmv.campuslf.model;

import java.time.LocalDate;

/** Validated publication data. Camera and GPS values are supplied by the View. */
public final class PublishDraft {
    public final String title;
    public final String description;
    public final String detailedLocation;
    public final String foundDate;
    public final String buildingId;
    public final String categoryId;
    public final String photoPath;
    public final Double latitude;
    public final Double longitude;

    public PublishDraft(String title, String description, String detailedLocation,
                        String foundDate, String buildingId, String categoryId,
                        String photoPath, Double latitude, Double longitude) {
        this.title = FoundItem.validateText(title, "Title", 3, FoundItem.MAX_TITLE_LENGTH);
        this.description = FoundItem.validateText(description, "Description", 5,
                FoundItem.MAX_DESCRIPTION_LENGTH);
        this.detailedLocation = FoundItem.validateText(detailedLocation, "Location", 2,
                FoundItem.MAX_LOCATION_LENGTH);
        final LocalDate date;
        try {
            date = LocalDate.parse(foundDate == null ? "" : foundDate);
        } catch (RuntimeException error) {
            throw new IllegalArgumentException("Choose a valid found date.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("The found date cannot be in the future.");
        }
        this.foundDate = date.toString();
        this.buildingId = required(buildingId, "Choose a building.");
        this.categoryId = required(categoryId, "Choose a category.");
        this.photoPath = required(photoPath, "Take a photograph before publishing.");
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("Both GPS coordinates must be supplied together.");
        }
        if (latitude != null && (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Invalid GPS coordinates.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private static String required(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public NewFoundItem withPhotoId(String photoId) {
        return new NewFoundItem(title, description, detailedLocation, foundDate,
                buildingId, categoryId, required(photoId, "The upload returned no photo ID."),
                latitude, longitude);
    }
}
