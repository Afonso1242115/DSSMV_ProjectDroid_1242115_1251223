package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;

public final class Category {
    @SerializedName("_id") private String id;
    @SerializedName(value = "name", alternate = {"category", "category_name", "nome"})
    private String name;

    public Category() { }
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name == null ? "" : name; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
