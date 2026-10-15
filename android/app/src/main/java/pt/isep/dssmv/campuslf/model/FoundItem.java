package pt.isep.dssmv.campuslf.model;

import java.time.LocalDate;

/** Representa um objeto encontrado no campus e aplica as regras básicas de validação. */
public final class FoundItem {
    public static final int MAX_TITLE_LENGTH = 80;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_LOCATION_LENGTH = 120;

    private final long id;
    private final String title;
    private final String description;
    private final String category;
    private final String foundLocation;
    private final LocalDate foundDate;

    public FoundItem(long id, String title, String description,
                     String category, String foundLocation, LocalDate foundDate) {
        if (id < 0) {
            throw new IllegalArgumentException("The identifier cannot be negative.");
        }

        this.id = id;
        this.title = validateText(title, "Title", 3, MAX_TITLE_LENGTH);
        this.description = validateText(
                description, "Description", 5, MAX_DESCRIPTION_LENGTH);
        this.category = validateText(category, "Category", 2, 40);
        this.foundLocation = validateText(
                foundLocation, "Location", 2, MAX_LOCATION_LENGTH);

        if (foundDate == null || foundDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("The found date cannot be in the future.");
        }
        this.foundDate = foundDate;
    }

    public static String validateText(String value, String fieldName,
                                      int minLength, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.length() < minLength || text.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + ": use between " + minLength + " and " + maxLength + " characters.");
        }
        return text;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getFoundLocation() {
        return foundLocation;
    }

    public LocalDate getFoundDate() {
        return foundDate;
    }
}
