package pt.isep.dssmv.campuslf.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;

/** One database instance per application process. Never retain an Activity context. */
@Database(entities = {FoundItemEntity.class, BuildingEntity.class, CategoryEntity.class},
        version = 1, exportSchema = true)
public abstract class CampusDatabase extends RoomDatabase {
    private static volatile CampusDatabase instance;

    public abstract CampusDao campusDao();

    public static CampusDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (CampusDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            CampusDatabase.class, "campuslf_room.db").build();
                }
            }
        }
        return instance;
    }
}
