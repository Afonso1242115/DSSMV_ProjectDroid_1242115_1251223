package pt.isep.dssmv.campuslf.util;

import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import pt.isep.dssmv.campuslf.BuildConfig;

/** Images use the same configured service and restricted key, without placing the key in a URL. */
public final class MediaImages {
    private MediaImages() { }

    public static void load(ImageView target, String photoId) {
        if (photoId == null || photoId.isEmpty()) {
            Glide.with(target).clear(target);
            target.setImageResource(android.R.drawable.ic_menu_camera);
            return;
        }
        String url = BuildConfig.RESTDB_BASE_URL + "media/" + Uri.encode(photoId) + "?s=w";
        GlideUrl image = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("x-apikey", BuildConfig.RESTDB_API_KEY).build());
        Glide.with(target).load(image).placeholder(android.R.drawable.ic_menu_camera)
                .error(android.R.drawable.ic_menu_report_image).into(target);
    }
}
