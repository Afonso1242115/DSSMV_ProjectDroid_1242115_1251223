package pt.isep.dssmv.campuslf.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class CampusDatabaseMigrationTest {
    @Test public void migratesInstalledCacheWithoutLosingLegacyPhotoOrGps() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String name = "campuslf-migration-test.db";
        context.deleteDatabase(name);
        CampusDatabase migrated = null;
        try {
            try (SQLiteDatabase old = context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null)) {
                old.execSQL("CREATE TABLE found_items_cache (id TEXT NOT NULL PRIMARY KEY, "
                        + "title TEXT NOT NULL, description TEXT NOT NULL, detailedLocation TEXT NOT NULL, "
                        + "foundDate TEXT NOT NULL, buildingId TEXT NOT NULL, buildingName TEXT NOT NULL, "
                        + "categoryId TEXT NOT NULL, categoryName TEXT NOT NULL, photoId TEXT, "
                        + "latitude REAL, longitude REAL)");
                old.execSQL("CREATE INDEX index_found_items_cache_buildingId ON found_items_cache (buildingId)");
                old.execSQL("CREATE INDEX index_found_items_cache_categoryId ON found_items_cache (categoryId)");
                old.execSQL("CREATE TABLE buildings (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL)");
                old.execSQL("CREATE TABLE categories (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)");
                old.execSQL("INSERT INTO found_items_cache VALUES "
                        + "('old', 'Keys', 'Found keys', 'Room F201', '2026-11-01', 'b1', 'F', "
                        + "'c1', 'Keys', 'legacy-photo', 41.178, -8.608)");
                old.setVersion(1);
            }
            migrated = Room.databaseBuilder(context, CampusDatabase.class, name)
                    .addMigrations(CampusDatabase.MIGRATION_1_2).allowMainThreadQueries().build();
            try (Cursor row = migrated.getOpenHelper().getWritableDatabase().query(
                    "SELECT photoId, photoBase64, latitude, longitude FROM found_items_cache WHERE id = 'old'")) {
                assertTrue(row.moveToFirst());
                assertEquals("legacy-photo", row.getString(0));
                assertTrue(row.isNull(1));
                assertEquals(41.178, row.getDouble(2), 0.000001);
                assertEquals(-8.608, row.getDouble(3), 0.000001);
            }
        } finally {
            if (migrated != null) migrated.close();
            context.deleteDatabase(name);
        }
    }
}
