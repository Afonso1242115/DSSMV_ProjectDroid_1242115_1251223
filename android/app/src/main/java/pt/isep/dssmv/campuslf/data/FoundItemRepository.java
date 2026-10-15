package pt.isep.dssmv.campuslf.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import pt.isep.dssmv.campuslf.model.FoundItem;

/** Persistência local temporária enquanto a API ainda não está implementada. */
public final class FoundItemRepository {
    private static final String DATABASE_NAME = "campus_lf.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FOUND_ITEMS = "found_items";

    private final DatabaseHelper databaseHelper;

    public FoundItemRepository(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public long add(FoundItem item) {
        ContentValues values = new ContentValues();
        values.put("title", item.getTitle());
        values.put("description", item.getDescription());
        values.put("category", item.getCategory());
        values.put("found_location", item.getFoundLocation());
        values.put("found_date", item.getFoundDate().toString());

        return databaseHelper.getWritableDatabase().insert(
                TABLE_FOUND_ITEMS, null, values);
    }

    public List<FoundItem> listAll() {
        List<FoundItem> items = new ArrayList<>();

        try (Cursor cursor = databaseHelper.getReadableDatabase().query(
                TABLE_FOUND_ITEMS,
                null,
                null,
                null,
                null,
                null,
                "id DESC")) {

            while (cursor.moveToNext()) {
                items.add(readItem(cursor));
            }
        }

        return items;
    }

    public FoundItem getById(long id) {
        try (Cursor cursor = databaseHelper.getReadableDatabase().query(
                TABLE_FOUND_ITEMS,
                null,
                "id = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null,
                "1")) {

            return cursor.moveToFirst() ? readItem(cursor) : null;
        }
    }

    private FoundItem readItem(Cursor cursor) {
        return new FoundItem(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("found_location")),
                LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("found_date")))
        );
    }

    private static final class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE " + TABLE_FOUND_ITEMS + " ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "title TEXT NOT NULL, "
                            + "description TEXT NOT NULL, "
                            + "category TEXT NOT NULL, "
                            + "found_location TEXT NOT NULL, "
                            + "found_date TEXT NOT NULL"
                            + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            // Para já não existem migrações porque esta é a primeira versão da base de dados.
        }
    }
}
