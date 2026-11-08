package pt.isep.dssmv.campuslf.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.exifinterface.media.ExifInterface;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.isep.dssmv.campuslf.model.PhotoLimits;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class PhotoUtilsInstrumentedTest {
    @Test public void compressesDetailedCameraImageAndPreservesItsOrientation() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file = File.createTempFile("inline-photo-test", ".jpg", context.getCacheDir());
        Bitmap source = Bitmap.createBitmap(1600, 1200, Bitmap.Config.ARGB_8888);
        Bitmap result = null;
        try {
            int[] pixels = new int[1600 * 1200];
            Random random = new Random(42);
            for (int i = 0; i < pixels.length; i++) pixels[i] = 0xff000000 | random.nextInt(0x1000000);
            source.setPixels(pixels, 0, 1600, 0, 0, 1600, 1200);
            try (FileOutputStream output = new FileOutputStream(file)) {
                assertTrue(source.compress(Bitmap.CompressFormat.JPEG, 100, output));
            }
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_ORIENTATION,
                    Integer.toString(ExifInterface.ORIENTATION_ROTATE_90));
            exif.saveAttributes();
            long originalSize = file.length();
            assertTrue(originalSize > PhotoLimits.MAX_JPEG_BYTES);

            byte[] jpeg = PhotoUtils.compressedJpeg(file);
            assertTrue(jpeg.length > 0);
            assertTrue(jpeg.length <= PhotoLimits.MAX_JPEG_BYTES);
            result = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            assertNotNull(result);
            assertTrue(result.getHeight() > result.getWidth());
            assertTrue(Math.max(result.getHeight(), result.getWidth()) <= 1024);
            assertEquals(originalSize, file.length());
        } finally {
            if (result != null) result.recycle();
            source.recycle();
            file.delete();
        }
    }
}
