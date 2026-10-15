package pt.isep.dssmv.campuslf.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.model.FoundItem;

/** Adapta objetos FoundItem para as linhas da ListView. */
public final class FoundItemAdapter extends ArrayAdapter<FoundItem> {
    private final LayoutInflater inflater;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FoundItemAdapter(Context context, List<FoundItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.item_found_item, parent, false);
        }

        FoundItem item = getItem(position);
        if (item != null) {
            ((TextView) row.findViewById(R.id.itemTitleText)).setText(item.getTitle());
            ((TextView) row.findViewById(R.id.itemCategoryText)).setText(item.getCategory());
            ((TextView) row.findViewById(R.id.itemLocationText)).setText(item.getFoundLocation());
            ((TextView) row.findViewById(R.id.itemDateText)).setText(
                    item.getFoundDate().format(dateFormatter));
        }

        return row;
    }
}
