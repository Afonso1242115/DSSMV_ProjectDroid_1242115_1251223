package pt.isep.dssmv.campuslf.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "buildings")
public final class BuildingEntity {
    @PrimaryKey @NonNull public final String id;
    @NonNull public final String name;
    @NonNull public final String description;

    public BuildingEntity(@NonNull String id, @NonNull String name,
                          @NonNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
