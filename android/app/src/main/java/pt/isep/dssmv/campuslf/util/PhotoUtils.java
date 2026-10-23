package pt.isep.dssmv.campuslf.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/** Resize and compress the image to fit the restdb.io free-tier file limit. */
public final class PhotoUtils {
    private static final int MAX_DIMENSION = 1280;
    private static final int MAX_BYTES = 950_000;
    private PhotoUtils() { }

    public static byte[] compressedJpeg(File file) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (options.outWidth <= 0 || options.outHeight <= 0)
            throw new IOException("Photo file is invalid. Take another photograph.");
        options.inSampleSize = 1;
        while (options.outWidth / options.inSampleSize > MAX_DIMENSION * 2 ||
                options.outHeight / options.inSampleSize > MAX_DIMENSION * 2)
            options.inSampleSize *= 2;
        options.inJustDecodeBounds = false;
        Bitmap decoded = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (decoded == null) throw new IOException("Photo could not be read. Take another photograph.");
        Bitmap photo = decoded;
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int angle = orientation == ExifInterface.ORIENTATION_ROTATE_90 ? 90 :
                    orientation == ExifInterface.ORIENTATION_ROTATE_180 ? 180 :
                            orientation == ExifInterface.ORIENTATION_ROTATE_270 ? 270 : 0;
            if (angle != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);
                photo = Bitmap.createBitmap(decoded, 0, 0,
                        decoded.getWidth(), decoded.getHeight(), matrix, true);
                if (photo != decoded) decoded.recycle();
            }
            int width = photo.getWidth(), height = photo.getHeight();
            if (Math.max(width, height) > MAX_DIMENSION) {
                float ratio = (float) MAX_DIMENSION / Math.max(width, height);
                Bitmap scaled = Bitmap.createScaledBitmap(photo,
                        Math.max(1, Math.round(width * ratio)),
                        Math.max(1, Math.round(height * ratio)), true);
                if (scaled != photo) photo.recycle();
                photo = scaled;
            }
            for (int quality = 85; quality >= 35; quality -= 10) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (!photo.compress(Bitmap.CompressFormat.JPEG, quality, bytes))
                    throw new IOException("Photo could not be compressed.");
                if (bytes.size() <= MAX_BYTES) return bytes.toByteArray();
            }
            throw new IOException("Photo is too large. Take another photograph.");
        } finally {
            if (!photo.isRecycled()) photo.recycle();
        }
    }
}
