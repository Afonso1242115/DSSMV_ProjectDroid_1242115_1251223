package pt.isep.dssmv.campuslf.data.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.model.Building;
import pt.isep.dssmv.campuslf.model.Category;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;

/** The HTTP DTOs and the Room rows are deliberately separate representations. */
public final class ItemMapper {
    private ItemMapper() { }

    public static List<BuildingEntity> buildings(List<Building> source) {
        List<BuildingEntity> result = new ArrayList<>();
        for (Building value : source) {
            String id = requireId(value.getId());
            if (value.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("A building has no name. Check its name field in restdb.io.");
            }
            result.add(new BuildingEntity(id, value.getName(), value.getDescription()));
        }
        return result;
    }

    public static List<CategoryEntity> categories(List<Category> source) {
        List<CategoryEntity> result = new ArrayList<>();
        for (Category value : source) {
            String id = requireId(value.getId());
            if (value.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("A category has no name. Check its name field in restdb.io.");
            }
            result.add(new CategoryEntity(id, value.getName()));
        }
        return result;
    }

    public static FoundItemEntity item(RemoteFoundItem item, List<BuildingEntity> buildings,
                                       List<CategoryEntity> categories) {
        String buildingId = empty(item.getBuildingId());
        String categoryId = empty(item.getCategoryId());
        Map<String, String> buildingNames = new HashMap<>();
        for (BuildingEntity building : buildings) buildingNames.put(building.id, building.name);
        Map<String, String> categoryNames = new HashMap<>();
        for (CategoryEntity category : categories) categoryNames.put(category.id, category.name);
        String buildingName = buildingNames.getOrDefault(buildingId, item.getBuildingName());
        String categoryName = categoryNames.getOrDefault(categoryId, item.getCategoryName());
        return new FoundItemEntity(requireId(item.getId()), item.getTitle(), item.getDescription(),
                item.getDetailedLocation(), item.getFoundDate(), buildingId, buildingName,
                categoryId, categoryName, item.getFirstPhotoId(), item.getLatitude(), item.getLongitude());
    }

    public static List<FoundItemEntity> items(List<RemoteFoundItem> source,
                                              List<BuildingEntity> buildings,
                                              List<CategoryEntity> categories) {
        List<FoundItemEntity> result = new ArrayList<>();
        for (RemoteFoundItem value : source) result.add(item(value, buildings, categories));
        return result;
    }

    private static String empty(String value) { return value == null ? "" : value; }

    private static String requireId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The server returned a record without its _id.");
        }
        return value;
    }
}
