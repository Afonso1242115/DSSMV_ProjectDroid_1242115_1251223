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
            String name = value.getName().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("A building has no name. Check its name field in restdb.io.");
            }
            result.add(new BuildingEntity(id, name, value.getDescription()));
        }
        return result;
    }

    public static List<CategoryEntity> categories(List<Category> source) {
        List<CategoryEntity> result = new ArrayList<>();
        for (Category value : source) {
            String id = requireId(value.getId());
            String name = value.getName().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("A category has no name. Check its name field in restdb.io.");
            }
            result.add(new CategoryEntity(id, name));
        }
        return result;
    }

    public static FoundItemEntity item(RemoteFoundItem source, List<BuildingEntity> buildings,
                                       List<CategoryEntity> categories) {
        return mapItem(source, indexBuildings(buildings), indexCategories(categories));
    }

    /** Pre-index lookups once for every remote page/snapshot. */
    public static List<FoundItemEntity> items(List<RemoteFoundItem> source,
                                              List<BuildingEntity> buildings,
                                              List<CategoryEntity> categories) {
        Map<String, String> buildingNames = indexBuildings(buildings);
        Map<String, String> categoryNames = indexCategories(categories);
        List<FoundItemEntity> result = new ArrayList<>(source.size());
        for (RemoteFoundItem value : source) {
            result.add(mapItem(value, buildingNames, categoryNames));
        }
        return result;
    }

    private static FoundItemEntity mapItem(RemoteFoundItem item,
                                           Map<String, String> buildingNames,
                                           Map<String, String> categoryNames) {
        String buildingId = empty(item.getBuildingId());
        String categoryId = empty(item.getCategoryId());
        String buildingName = buildingNames.getOrDefault(buildingId, item.getBuildingName());
        String categoryName = categoryNames.getOrDefault(categoryId, item.getCategoryName());
        return new FoundItemEntity(requireId(item.getId()), item.getTitle(), item.getDescription(),
                item.getDetailedLocation(), item.getFoundDate(), buildingId, buildingName,
                categoryId, categoryName, item.getFirstPhotoId(), item.getPhotoBase64(),
                item.getLatitude(), item.getLongitude());
    }

    private static Map<String, String> indexBuildings(List<BuildingEntity> buildings) {
        Map<String, String> names = new HashMap<>();
        for (BuildingEntity building : buildings) names.put(building.id, building.name);
        return names;
    }

    private static Map<String, String> indexCategories(List<CategoryEntity> categories) {
        Map<String, String> names = new HashMap<>();
        for (CategoryEntity category : categories) names.put(category.id, category.name);
        return names;
    }

    private static String empty(String value) { return value == null ? "" : value; }

    private static String requireId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("The server returned a record without its _id.");
        }
        return value;
    }
}
