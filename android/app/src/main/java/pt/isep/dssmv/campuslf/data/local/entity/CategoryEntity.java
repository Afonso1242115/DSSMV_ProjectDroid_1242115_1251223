package pt.isep.dssmv.campuslf.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public final class CategoryEntity {
    @PrimaryKey @NonNull public final String id;
    @NonNull public final String name;

    public CategoryEntity(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}
