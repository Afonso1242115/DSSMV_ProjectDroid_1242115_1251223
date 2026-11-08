package pt.isep.dssmv.campuslf.data.local;

import org.junit.Test;
import java.util.Collections;
import java.util.List;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.model.Building;
import pt.isep.dssmv.campuslf.model.Category;
import pt.isep.dssmv.campuslf.model.RemoteFoundItem;
import static org.junit.Assert.*;

public final class ItemMapperTest {
    @Test public void resolvesNamesUsingLocalReferences() {
        Building building = new Building("b1", "Embedded label", "");
        Category category = new Category("c1", "Embedded category");
        RemoteFoundItem remote = new RemoteFoundItem();
        remote.setId("item1");
        remote.setTitle("Student ID");
        remote.setBuilding(Collections.singletonList(building));
        remote.setCategory(Collections.singletonList(category));
        List<BuildingEntity> buildings = Collections.singletonList(
                new BuildingEntity("b1", "Building F", ""));
        List<CategoryEntity> categories = Collections.singletonList(
                new CategoryEntity("c1", "Documents"));

        FoundItemEntity mapped = ItemMapper.items(Collections.singletonList(remote),
                buildings, categories).get(0);
        assertEquals("item1", mapped.id);
        assertEquals("Building F", mapped.buildingName);
        assertEquals("Documents", mapped.categoryName);
        assertEquals("Student ID", mapped.title);
    }

    @Test public void trimsReferenceDisplayNames() {
        BuildingEntity building = ItemMapper.buildings(Collections.singletonList(
                new Building("b1", "  Building F  ", ""))).get(0);
        assertEquals("Building F", building.name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRemoteItemsWithoutIds() {
        ItemMapper.items(Collections.singletonList(new RemoteFoundItem()),
                Collections.emptyList(), Collections.emptyList());
    }

    @Test public void keepsInlinePhotoAndGpsAfterRemoteRefresh() {
        RemoteFoundItem remote = new RemoteFoundItem();
        remote.setId("item-photo");
        remote.setPhotoBase64("photo-data");
        remote.setLatitude(41.178);
        remote.setLongitude(-8.608);
        FoundItemEntity mapped = ItemMapper.item(remote, Collections.emptyList(), Collections.emptyList());
        assertEquals("photo-data", mapped.photoBase64);
        assertEquals(41.178, mapped.latitude, 0.000001);
        assertEquals(-8.608, mapped.longitude, 0.000001);
        assertNull(mapped.photoId);
    }

    @Test public void keepsLegacyMediaIdWhenInlinePhotoIsAbsent() {
        RemoteFoundItem remote = new RemoteFoundItem();
        remote.setId("old-item");
        remote.setPhotos(Collections.singletonList("legacy-photo"));
        FoundItemEntity mapped = ItemMapper.item(remote, Collections.emptyList(), Collections.emptyList());
        assertEquals("legacy-photo", mapped.photoId);
        assertNull(mapped.photoBase64);
    }
}
