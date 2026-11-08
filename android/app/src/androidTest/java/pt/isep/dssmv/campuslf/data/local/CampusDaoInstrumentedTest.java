package pt.isep.dssmv.campuslf.data.local;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class CampusDaoInstrumentedTest {
    @Rule public InstantTaskExecutorRule executor = new InstantTaskExecutorRule();
    private CampusDatabase database;
    private CampusDao dao;

    @Before public void openDatabase() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, CampusDatabase.class)
                .allowMainThreadQueries().build();
        dao = database.campusDao();
    }

    @After public void closeDatabase() {
        if (database != null) database.close();
    }

    @Test public void textSearchCombinesWithBuildingAndCategory() throws InterruptedException {
        dao.upsertItem(item("1", "Blue headphones", "bF", "cElectronics"));
        dao.upsertItem(item("2", "Student ID", "bF", "cDocuments"));
        dao.upsertItem(item("3", "Blue notebook", "bB", "cDocuments"));

        List<FoundItemEntity> matches = await(dao.observeItems("bF", "cElectronics", "BLUE"));
        assertEquals(1, matches.size());
        assertEquals("1", matches.get(0).id);
        assertEquals(3, await(dao.observeItems(null, null, "")).size());
        assertTrue(await(dao.observeItems("bF", "cElectronics", "notebook")).isEmpty());
    }

    @Test public void replacesCacheOnlyWithProvidedSnapshot() throws InterruptedException {
        dao.upsertItem(item("old", "Old item", "bF", "cDocuments"));
        dao.replaceSnapshot(java.util.Collections.singletonList(
                item("new", "Fresh item", "bF", "cDocuments")),
                java.util.Collections.emptyList(), java.util.Collections.emptyList());
        List<FoundItemEntity> result = await(dao.observeItems(null, null, ""));
        assertEquals(1, result.size());
        assertEquals("new", result.get(0).id);
    }

    @Test public void retainsFullInlinePhotoAndGpsInOfflineCache() throws InterruptedException {
        String photo = java.util.Base64.getEncoder().encodeToString(new byte[60_000]);
        dao.upsertItem(new FoundItemEntity("photo-item", "Keys", "Found keys", "Room F201",
                "2026-11-01", "b1", "F", "c1", "Keys", null, photo, 41.178, -8.608));
        FoundItemEntity cached = await(dao.observeItem("photo-item"));
        assertEquals(photo, cached.photoBase64);
        assertNull(cached.photoId);
        assertEquals(41.178, cached.latitude, 0.000001);
        assertEquals(-8.608, cached.longitude, 0.000001);
    }

    private static FoundItemEntity item(String id, String title, String building, String category) {
        return new FoundItemEntity(id, title, "A found item", "Library", "2026-11-01",
                building, "Building", category, "Category", null, null, null, null);
    }

    private static <T> T await(LiveData<T> source) throws InterruptedException {
        AtomicReference<T> value = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = result -> {
            value.set(result);
            latch.countDown();
        };
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                () -> source.observeForever(observer));
        try {
            assertTrue("LiveData did not emit a value", latch.await(5, TimeUnit.SECONDS));
            return value.get();
        } finally {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(
                    () -> source.removeObserver(observer));
        }
    }
}
