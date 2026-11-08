package pt.isep.dssmv.campuslf.util;

import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import pt.isep.dssmv.campuslf.BuildConfig;
import pt.isep.dssmv.campuslf.R;

/** Prefer the photo saved in the record; older Media Archive IDs remain readable. */
public final class MediaImages {
    private MediaImages() { }

    public static void load(ImageView target, String photoId, String photoBase64) {
        if (photoBase64 != null && !photoBase64.isEmpty()) {
            // Glide decodes the data URL on its worker, just like downloaded images.
            Glide.with(target).load("data:image/jpeg;base64," + photoBase64)
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .error(R.drawable.ic_photo_unavailable).into(target);
            return;
        }
        if (photoId == null || photoId.isEmpty()) {
            Glide.with(target).clear(target);
            target.setImageResource(R.drawable.ic_photo_placeholder);
            return;
        }
        String url = BuildConfig.RESTDB_MEDIA_BASE_URL + "media/" + Uri.encode(photoId) + "?s=w";
        GlideUrl image = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("x-apikey", BuildConfig.RESTDB_API_KEY).build());
        Glide.with(target).load(image).placeholder(R.drawable.ic_photo_placeholder)
                .error(R.drawable.ic_photo_unavailable).into(target);
    }
}
