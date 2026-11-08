package pt.isep.dssmv.campuslf.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import pt.isep.dssmv.campuslf.model.PhotoLimits;

// reduz a fotografia para caber no registo enviado ao servidor
public final class PhotoUtils {
    private static final int MAX_DIMENSION = 1024;
    private static final int MIN_DIMENSION = 256;
    private PhotoUtils() { }

    public static byte[] compressedJpeg(File file) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // lê o tamanho da fotografia antes de a carregar para a memória
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (options.outWidth <= 0 || options.outHeight <= 0)
            throw new IOException("Photo file is invalid. Take another photograph.");
        options.inSampleSize = 1;
        // reduz o tamanho durante a leitura para poupar memória
        while (options.outWidth / options.inSampleSize > MAX_DIMENSION * 2 ||
                options.outHeight / options.inSampleSize > MAX_DIMENSION * 2)
            options.inSampleSize *= 2;
        options.inJustDecodeBounds = false;
        Bitmap decoded = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (decoded == null) throw new IOException("Photo could not be read. Take another photograph.");
        Bitmap photo = decoded;
        try {
            // corrige a orientação gravada pela câmara
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
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // reduz a qualidade e depois a resolução até a fotografia caber no limite
            while (true) {
                for (int quality = 85; quality >= 45; quality -= 10) {
                    bytes.reset();
                    if (!photo.compress(Bitmap.CompressFormat.JPEG, quality, bytes))
                        throw new IOException("Photo could not be compressed.");
                    if (bytes.size() <= PhotoLimits.MAX_JPEG_BYTES) return bytes.toByteArray();
                }
                int longest = Math.max(photo.getWidth(), photo.getHeight());
                if (longest <= MIN_DIMENSION) {
                    throw new IOException("Photo is too large. Take another photograph.");
                }
                int next = Math.max(MIN_DIMENSION, Math.round(longest * 0.75f));
                float ratio = (float) next / longest;
                Bitmap scaled = Bitmap.createScaledBitmap(photo,
                        Math.max(1, Math.round(photo.getWidth() * ratio)),
                        Math.max(1, Math.round(photo.getHeight() * ratio)), true);
                if (scaled != photo) photo.recycle();
                photo = scaled;
            }
        } finally {
            if (!photo.isRecycled()) photo.recycle();
        }
    }
}
