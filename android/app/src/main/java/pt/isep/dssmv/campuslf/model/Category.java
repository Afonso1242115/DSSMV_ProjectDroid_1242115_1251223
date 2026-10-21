package pt.isep.dssmv.campuslf.model;

import com.google.gson.annotations.SerializedName;

public final class Category {
    @SerializedName("_id") private String id;
    @SerializedName("name") private String name;

    public String getId() { return id; }
    public String getName() { return name == null ? "" : name; }
}
