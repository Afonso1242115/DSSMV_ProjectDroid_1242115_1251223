package com.campuslf.app.model;

import java.time.LocalDate;

/** Representa um objeto encontrado no campus. */
public final class FoundItem {
    private final long id;
    private final String title;
    private final String description;
    private final String category;
    private final String foundLocation;
    private final LocalDate foundDate;

    public FoundItem(long id, String title, String description,
                     String category, String foundLocation, LocalDate foundDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.foundLocation = foundLocation;
        this.foundDate = foundDate;
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
