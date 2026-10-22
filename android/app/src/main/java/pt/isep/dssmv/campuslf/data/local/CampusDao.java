package pt.isep.dssmv.campuslf.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;

/** LiveData queries are asynchronous; synchronous writes are called only on an IO scheduler. */
@Dao
public abstract class CampusDao {
    @Query("SELECT * FROM found_items_cache WHERE "
            + "(:buildingId IS NULL OR buildingId = :buildingId) AND "
            + "(:categoryId IS NULL OR categoryId = :categoryId) "
            + "ORDER BY foundDate DESC, id DESC")
    public abstract LiveData<List<FoundItemEntity>> observeItems(String buildingId, String categoryId);

    @Query("SELECT * FROM found_items_cache WHERE id = :id LIMIT 1")
    public abstract LiveData<FoundItemEntity> observeItem(String id);

    @Query("SELECT * FROM buildings ORDER BY name COLLATE NOCASE")
    public abstract LiveData<List<BuildingEntity>> observeBuildings();

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    public abstract LiveData<List<CategoryEntity>> observeCategories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertItem(FoundItemEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertItems(List<FoundItemEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertBuildings(List<BuildingEntity> buildings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void upsertCategories(List<CategoryEntity> categories);

    @Query("DELETE FROM found_items_cache")
    public abstract void clearItems();

    @Query("DELETE FROM found_items_cache WHERE id = :id")
    public abstract void deleteItem(String id);

    @Query("DELETE FROM buildings")
    public abstract void clearBuildings();

    @Query("DELETE FROM categories")
    public abstract void clearCategories();

    @Query("SELECT * FROM buildings")
    public abstract List<BuildingEntity> readBuildings();

    @Query("SELECT * FROM categories")
    public abstract List<CategoryEntity> readCategories();

    @Transaction
    public void replaceReferences(List<BuildingEntity> buildings, List<CategoryEntity> categories) {
        clearBuildings();
        clearCategories();
        upsertBuildings(buildings);
        upsertCategories(categories);
    }

    /** Called only after all remote pages and lookups have been received successfully. */
    @Transaction
    public void replaceSnapshot(List<FoundItemEntity> items, List<BuildingEntity> buildings,
                                List<CategoryEntity> categories) {
        replaceReferences(buildings, categories);
        clearItems();
        upsertItems(items);
    }
}
