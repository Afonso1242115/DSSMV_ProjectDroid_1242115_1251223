package pt.isep.dssmv.campuslf.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pt.isep.dssmv.campuslf.model.Building;
import pt.isep.dssmv.campuslf.model.Category;

/** A lookup may contain embedded records or just IDs. Both forms preserve its identity. */
public final class RestDbJson {
    private RestDbJson() { }

    public static Gson create() {
        JsonDeserializer<Building> buildings = (json, type, context) -> {
            if (json.isJsonPrimitive()) return new Building(json.getAsString(), "", "");
            JsonObject value = json.getAsJsonObject();
            return new Building(text(value, "_id"), first(value, "name", "building", "building_name", "nome"),
                    text(value, "description"));
        };
        JsonDeserializer<Category> categories = (json, type, context) -> {
            if (json.isJsonPrimitive()) return new Category(json.getAsString(), "");
            JsonObject value = json.getAsJsonObject();
            return new Category(text(value, "_id"), first(value, "name", "category", "category_name", "nome"));
        };
        return new GsonBuilder().registerTypeAdapter(Building.class, buildings)
                .registerTypeAdapter(Category.class, categories).create();
    }

    private static String first(JsonObject value, String... names) {
        for (String name : names) {
            String result = text(value, name);
            if (!result.isEmpty()) return result;
        }
        return "";
    }

    private static String text(JsonObject value, String name) {
        JsonElement element = value.get(name);
        return element == null || element.isJsonNull() ? "" : element.getAsString();
    }
}
