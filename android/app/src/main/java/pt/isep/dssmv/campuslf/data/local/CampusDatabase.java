package pt.isep.dssmv.campuslf.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;

// mantém uma única ligação à base de dados local
@Database(entities = {FoundItemEntity.class, BuildingEntity.class, CategoryEntity.class},
        version = 2, exportSchema = true)
public abstract class CampusDatabase extends RoomDatabase {
    private static volatile CampusDatabase instance;

    // acrescenta a fotografia aos dados locais sem apagar os registos existentes
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE found_items_cache ADD COLUMN photoBase64 TEXT");
        }
    };

    public abstract CampusDao campusDao();

    public static CampusDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (CampusDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            CampusDatabase.class, "campuslf_room.db")
                            .addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return instance;
    }
}
