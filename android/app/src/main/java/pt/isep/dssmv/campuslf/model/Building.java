package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;

public final class Building {
    @SerializedName("_id") private String id;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;

    public String getId() { return id; }
    public String getName() { return name == null ? "" : name; }
    public String getDescription() { return description == null ? "" : description; }
}
