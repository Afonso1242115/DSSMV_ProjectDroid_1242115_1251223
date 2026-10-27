package pt.isep.dssmv.campuslf.ui;

import android.content.Context;
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

    @Override public View getView(int position, View row, ViewGroup parent) {
        if (row == null) row = LayoutInflater.from(getContext()).inflate(R.layout.item_found_item, parent, false);
        FoundItemEntity item = getItem(position);
        if (item != null) {
            ((TextView) row.findViewById(R.id.itemTitleText)).setText(item.title);
            ((TextView) row.findViewById(R.id.itemCategoryText)).setText(item.categoryName);
            ((TextView) row.findViewById(R.id.itemLocationText)).setText(item.buildingName + " - " + item.detailedLocation);
            ((TextView) row.findViewById(R.id.itemDateText)).setText(item.foundDate);
            MediaImages.load((ImageView) row.findViewById(R.id.itemImage), item.photoId);
        }
        return row;
    }
}
