package pt.isep.dssmv.campuslf.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.util.MediaImages;

public final class FoundItemAdapter extends ArrayAdapter<FoundItemEntity> {
    public FoundItemAdapter(Context context, List<FoundItemEntity> items) { super(context, 0, items); }

    @NonNull
    @Override public View getView(int position, @Nullable View row, @NonNull ViewGroup parent) {
        if (row == null) row = LayoutInflater.from(getContext()).inflate(R.layout.item_found_item, parent, false);
        FoundItemEntity item = getItem(position);
        if (item != null) {
            ((TextView) row.findViewById(R.id.itemTitleText)).setText(item.title);
            ((TextView) row.findViewById(R.id.itemCategoryText)).setText(item.categoryName);
            ((TextView) row.findViewById(R.id.itemLocationText)).setText(
                    getContext().getString(R.string.item_location_format,
                            item.buildingName, item.detailedLocation));
            ((TextView) row.findViewById(R.id.itemDateText)).setText(item.foundDate);
            ImageView image = row.findViewById(R.id.itemImage);
            MediaImages.load(image, item.photoId);
        }
        return row;
    }
}
