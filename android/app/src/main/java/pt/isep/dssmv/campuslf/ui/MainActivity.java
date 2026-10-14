package com.campuslf.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.campuslf.app.R;
import com.campuslf.app.data.FoundItemRepository;
import com.campuslf.app.model.FoundItem;
import java.util.ArrayList;
import java.util.List;

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