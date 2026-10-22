package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;

public final class Building {
    @SerializedName("_id") private String id;
    @SerializedName(value = "name", alternate = {"building", "building_name", "nome"})
    private String name;
    @SerializedName("description") private String description;

    public Building() { }
    public Building(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name == null ? "" : name; }
    public String getDescription() { return description == null ? "" : description; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}
