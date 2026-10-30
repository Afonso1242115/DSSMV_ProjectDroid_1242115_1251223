package pt.isep.dssmv.campuslf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.viewmodel.MainViewModel;

/** View only: binds widgets, forwards actions and observes its ViewModel. */
public final class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private final ActivityResultLauncher<Intent> publisher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null
                        && result.getData().getBooleanExtra("refresh_required", false)) viewModel.refresh();
            });
    private FoundItemAdapter itemAdapter;
    private Spinner buildingSpinner;
    private Spinner categorySpinner;
    private List<BuildingEntity> buildings = new ArrayList<>();
    private List<CategoryEntity> categories = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        buildingSpinner = findViewById(R.id.buildingSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        ListView list = findViewById(R.id.foundItemsList);
        list.setEmptyView(findViewById(R.id.emptyListText));
        itemAdapter = new FoundItemAdapter(this, new ArrayList<>());
        list.setAdapter(itemAdapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra(ItemDetailsActivity.EXTRA_ITEM_ID, itemAdapter.getItem(position).id);
            startActivity(intent);
        });
        findViewById(R.id.refreshButton).setOnClickListener(view -> viewModel.refresh());
        findViewById(R.id.addItemButton).setOnClickListener(view ->
                publisher.launch(new Intent(this, AddItemActivity.class)));

        viewModel.getItems().observe(this, items -> {
            itemAdapter.clear();
            if (items != null) itemAdapter.addAll(items);
            int size = items == null ? 0 : items.size();
            ((TextView) findViewById(R.id.itemCountText)).setText(
                    getResources().getQuantityString(R.plurals.saved_item_count, size, size));
        });
        viewModel.getBuildings().observe(this, this::renderBuildings);
        viewModel.getCategories().observe(this, this::renderCategories);
        viewModel.getOperation().observe(this, state -> {
            ((TextView) findViewById(R.id.statusText)).setText(state.message);
            findViewById(R.id.refreshButton).setEnabled(!state.isRunning());
            findViewById(R.id.refreshProgress).setVisibility(state.isRunning() ? View.VISIBLE : View.GONE);
        });
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFilter(parent);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        };
        buildingSpinner.setOnItemSelectedListener(listener);
        categorySpinner.setOnItemSelectedListener(listener);
    }

    private void renderBuildings(List<BuildingEntity> values) {
        buildings = values == null ? new ArrayList<>() : values;
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.all_buildings));
        int selected = 0;
        for (int i = 0; i < buildings.size(); i++) {
            labels.add(buildings.get(i).name);
            if (Objects.equals(buildings.get(i).id, viewModel.getBuildingId())) selected = i + 1;
        }
        setChoices(buildingSpinner, labels, selected);
    }

    private void renderCategories(List<CategoryEntity> values) {
        categories = values == null ? new ArrayList<>() : values;
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.all_categories));
        int selected = 0;
        for (int i = 0; i < categories.size(); i++) {
            labels.add(categories.get(i).name);
            if (Objects.equals(categories.get(i).id, viewModel.getCategoryId())) selected = i + 1;
        }
        setChoices(categorySpinner, labels, selected);
    }

    private void setChoices(Spinner spinner, List<String> labels, int selected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selected, false);
    }

    private void updateFilter(AdapterView<?> changed) {
        // Updating one spinner must not clear the other saved filter before its Room query emits.
        if (changed == buildingSpinner) {
            int index = buildingSpinner.getSelectedItemPosition() - 1;
            viewModel.setFilters(index >= 0 && index < buildings.size() ? buildings.get(index).id : null,
                    viewModel.getCategoryId());
        } else {
            int index = categorySpinner.getSelectedItemPosition() - 1;
            viewModel.setFilters(viewModel.getBuildingId(),
                    index >= 0 && index < categories.size() ? categories.get(index).id : null);
        }
    }
}
