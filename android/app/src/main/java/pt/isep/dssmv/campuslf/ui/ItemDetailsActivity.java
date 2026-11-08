package pt.isep.dssmv.campuslf.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Locale;
import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.data.local.entity.FoundItemEntity;
import pt.isep.dssmv.campuslf.util.MediaImages;
import pt.isep.dssmv.campuslf.viewmodel.ItemDetailsViewModel;

// mostra os detalhes do objeto e as ações de mapa e partilha
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
            setText(R.id.dateText, DateLabels.format(item.foundDate));
            boolean hasGps = item.latitude != null && item.longitude != null;
            setText(R.id.coordinatesText, !hasGps
                    ? getString(R.string.no_coordinates)
                    : String.format(Locale.US, "%.6f, %.6f", item.latitude, item.longitude));
            findViewById(R.id.openMapButton).setVisibility(hasGps ? View.VISIBLE : View.GONE);
            findViewById(R.id.openMapButton).setOnClickListener(view -> openMap(item));
            findViewById(R.id.shareItemButton).setOnClickListener(view -> shareItem(item));
            MediaImages.load((ImageView) findViewById(R.id.detailImage), item.photoId, item.photoBase64);
        });
        viewModel.getOperation().observe(this, state -> {
            setText(R.id.statusText, state.message);
            findViewById(R.id.refreshButton).setEnabled(!state.isRunning());
        });
    }

    // abre as coordenadas na aplicação de mapas
    private void openMap(FoundItemEntity item) {
        if (item.latitude == null || item.longitude == null) return;
        String point = String.format(Locale.US, "%.6f,%.6f", item.latitude, item.longitude);
        Uri uri = Uri.parse("geo:0,0?q=" + point + "(" + Uri.encode(item.title) + ")");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, R.string.map_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    // prepara o texto apresentado no menu de partilha
    private void shareItem(FoundItemEntity item) {
        String message = getString(R.string.share_item_text, item.title,
                item.description, item.buildingName, item.detailedLocation, item.foundDate);
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(send, getString(R.string.share_item)));
    }

    private void setText(int id, String value) { ((TextView) findViewById(id)).setText(value); }
}
