package pt.isep.dssmv.campuslf.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Locale;
import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.util.MediaImages;
import pt.isep.dssmv.campuslf.viewmodel.ItemDetailsViewModel;

public final class ItemDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID = ItemDetailsViewModel.ITEM_ID_KEY;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        ItemDetailsViewModel viewModel = new ViewModelProvider(this).get(ItemDetailsViewModel.class);
        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.refreshButton).setOnClickListener(view -> viewModel.refresh());
        viewModel.getItem().observe(this, item -> {
            findViewById(R.id.detailsContent).setVisibility(item == null ? View.GONE : View.VISIBLE);
            findViewById(R.id.detailsEmptyText).setVisibility(item == null ? View.VISIBLE : View.GONE);
            if (item == null) return;
            setText(R.id.titleText, item.title);
            setText(R.id.descriptionText, item.description);
            setText(R.id.categoryText, item.categoryName);
            setText(R.id.buildingText, item.buildingName);
            setText(R.id.locationText, item.detailedLocation);
            setText(R.id.dateText, item.foundDate);
            setText(R.id.coordinatesText, item.latitude == null || item.longitude == null
                    ? getString(R.string.no_coordinates)
                    : String.format(Locale.US, "%.6f, %.6f", item.latitude, item.longitude));
            MediaImages.load((ImageView) findViewById(R.id.detailImage), item.photoId);
        });
        viewModel.getOperation().observe(this, state -> {
            setText(R.id.statusText, state.message);
            findViewById(R.id.refreshButton).setEnabled(!state.isRunning());
        });
    }

    private void setText(int id, String value) { ((TextView) findViewById(id)).setText(value); }
}
