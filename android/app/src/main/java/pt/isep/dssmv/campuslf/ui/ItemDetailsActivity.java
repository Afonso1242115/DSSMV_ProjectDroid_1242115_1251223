package com.campuslf.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.campuslf.app.R;
import com.campuslf.app.data.FoundItemRepository;
import com.campuslf.app.model.FoundItem;
import java.time.format.DateTimeFormatter;

/** Mostra todos os dados do objeto selecionado. */
public final class ItemDetailsActivity extends Activity {
    public static final String EXTRA_ITEM_ID = "com.campuslf.app.FOUND_ITEM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId <= 0) {
            showNotFound();
            return;
        }

        FoundItemRepository repository = new FoundItemRepository(this);
        FoundItem item = repository.getById(itemId);
        if (item == null) {
            showNotFound();
            return;
        }

        setText(R.id.titleText, item.getTitle());
        setText(R.id.descriptionText, item.getDescription());
        setText(R.id.categoryText, item.getCategory());
        setText(R.id.locationText, item.getFoundLocation());
        setText(R.id.dateText, item.getFoundDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void setText(int viewId, String value) {
        ((TextView) findViewById(viewId)).setText(value);
    }

    private void showNotFound() {
        Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show();
        finish();
    }
}
