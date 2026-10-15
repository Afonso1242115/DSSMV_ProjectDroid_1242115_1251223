package pt.isep.dssmv.campuslf.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.data.FoundItemRepository;
import pt.isep.dssmv.campuslf.model.FoundItem;

/** Ecrã principal da aplicação. */
public final class MainActivity extends Activity {
    private FoundItemRepository repository;
    private FoundItemAdapter itemAdapter;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new FoundItemRepository(this);
        statusText = findViewById(R.id.statusText);

        ListView foundItemsList = findViewById(R.id.foundItemsList);
        foundItemsList.setEmptyView(findViewById(R.id.emptyListText));

        itemAdapter = new FoundItemAdapter(this, new ArrayList<>());
        foundItemsList.setAdapter(itemAdapter);

        findViewById(R.id.addItemButton).setOnClickListener(view ->
                startActivity(new Intent(this, AddItemActivity.class)));

        foundItemsList.setOnItemClickListener((parent, view, position, id) -> {
            FoundItem item = itemAdapter.getItem(position);
            if (item == null) {
                return;
            }

            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra(ItemDetailsActivity.EXTRA_ITEM_ID, item.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        try {
            List<FoundItem> items = repository.listAll();
            itemAdapter.clear();
            itemAdapter.addAll(items);
            statusText.setText(getResources().getQuantityString(
                    R.plurals.saved_item_count,
                    items.size(),
                    items.size()));
        } catch (RuntimeException exception) {
            Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
        }
    }
}
