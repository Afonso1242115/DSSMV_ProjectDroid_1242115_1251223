package pt.isep.dssmv.campuslf.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;

import pt.isep.dssmv.campuslf.R;

/** Mostra todos os dados do objeto selecionado. */
public final class ItemDetailsActivity extends BaseActivity {
    public static final String EXTRA_ITEM_ID = "pt.isep.dssmv.campuslf.FOUND_ITEM_ID";

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

        runAsync(() -> repository.getById(itemId), item -> {
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
        }, exception -> {
            Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void setText(int viewId, String value) {
        ((TextView) findViewById(viewId)).setText(value);
    }

    private void showNotFound() {
        Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show();
        finish();
    }
}
