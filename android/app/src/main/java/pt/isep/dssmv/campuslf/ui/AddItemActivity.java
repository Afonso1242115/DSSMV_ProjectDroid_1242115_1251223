package pt.isep.dssmv.campuslf.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.data.local.entity.BuildingEntity;
import pt.isep.dssmv.campuslf.data.local.entity.CategoryEntity;
import pt.isep.dssmv.campuslf.viewmodel.AddItemViewModel;
import pt.isep.dssmv.campuslf.viewmodel.OperationState;

public final class AddItemActivity extends BaseActivity {
    private AddItemViewModel viewModel;
    private Spinner buildingSpinner;
    private Spinner categorySpinner;
    private List<BuildingEntity> buildings = new ArrayList<>();
    private List<CategoryEntity> categories = new ArrayList<>();
    private CancellationSignal locationCancellation;
    private final ActivityResultLauncher<String[]> locationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), grants -> {
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) requestLocation();
                else viewModel.locationMessage("Location permission declined. You can still publish using the written location.");
            });
    private final ActivityResultLauncher<Uri> camera = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> viewModel.photoResult(success));

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);
        bindText(R.id.titleInput, "title");
        bindText(R.id.descriptionInput, "description");
        bindText(R.id.locationInput, "location");
        buildingSpinner = findViewById(R.id.buildingSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        findViewById(R.id.dateButton).setOnClickListener(view -> chooseDate());
        findViewById(R.id.photoButton).setOnClickListener(view -> takePhoto());
        findViewById(R.id.saveButton).setOnClickListener(view -> viewModel.publish());
        findViewById(R.id.cancelButton).setOnClickListener(view -> finish());
        findViewById(R.id.reloadReferencesButton).setOnClickListener(view -> viewModel.refreshReferences());
        findViewById(R.id.gpsButton).setOnClickListener(view -> {
            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) requestLocation();
            else locationPermission.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        });
        findViewById(R.id.clearGpsButton).setOnClickListener(view -> {
            if (locationCancellation != null) locationCancellation.cancel();
            locationCancellation = null;
            viewModel.clearCoordinates();
        });
        viewModel.getLocationStatus().observe(this, value ->
                ((TextView) findViewById(R.id.gpsStatusText)).setText(value));
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (!viewModel.isPublishing()) finish();
                else Toast.makeText(AddItemActivity.this, R.string.wait_for_publication, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getDate().observe(this, value -> ((Button) findViewById(R.id.dateButton)).setText(value));
        viewModel.getPhoto().observe(this, path -> {
            if (path != null && !path.isEmpty()) Glide.with(this).load(new File(path))
                    .into((ImageView) findViewById(R.id.photoPreview));
        });
        viewModel.getBuildings().observe(this, this::renderBuildings);
        viewModel.getCategories().observe(this, this::renderCategories);
        viewModel.getReferences().observe(this, state ->
                ((TextView) findViewById(R.id.lookupStatusText)).setText(state.message));
        viewModel.getPublication().observe(this, state -> {
            if (state.isRunning() && locationCancellation != null) {
                locationCancellation.cancel();
                locationCancellation = null;
                viewModel.locationMessage("Publishing with the location currently selected.");
            }
            ((TextView) findViewById(R.id.publishStatusText)).setText(state.message);
            setFormEnabled((ViewGroup) findViewById(R.id.formContent), !state.isRunning());
            updatePublishButton();
            if (state.status == OperationState.Status.ERROR)
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show();
        });
        viewModel.getPublished().observe(this, result -> {
            if (result == null) return;
            setResult(RESULT_OK, new Intent().putExtra("refresh_required", !result.cached));
            Toast.makeText(this, result.cached ? R.string.item_published : R.string.published_refresh,
                    Toast.LENGTH_LONG).show();
            finish();
        });
        buildingSpinner.setOnItemSelectedListener(selectionListener(true));
        categorySpinner.setOnItemSelectedListener(selectionListener(false));
    }

    private void bindText(int id, String key) {
        EditText input = findViewById(id);
        input.setText(viewModel.getText(key));
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence value, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence value, int start, int before, int count) {
                viewModel.setText(key, value.toString());
            }
            @Override public void afterTextChanged(Editable value) { }
        });
    }

    private AdapterView.OnItemSelectedListener selectionListener(boolean isBuilding) {
        return new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition() - 1;
                String value = isBuilding
                        ? (index >= 0 && index < buildings.size() ? buildings.get(index).id : "")
                        : (index >= 0 && index < categories.size() ? categories.get(index).id : "");
                viewModel.setText(isBuilding ? "building_id" : "category_id", value);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        };
    }

    private void renderBuildings(List<BuildingEntity> values) {
        buildings = values == null ? new ArrayList<>() : values;
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.choose_building));
        int selected = 0;
        for (int i = 0; i < buildings.size(); i++) {
            labels.add(buildings.get(i).name);
            if (buildings.get(i).id.equals(viewModel.getText("building_id"))) selected = i + 1;
        }
        setChoices(buildingSpinner, labels, selected);
        updatePublishButton();
    }

    private void renderCategories(List<CategoryEntity> values) {
        categories = values == null ? new ArrayList<>() : values;
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.choose_category));
        int selected = 0;
        for (int i = 0; i < categories.size(); i++) {
            labels.add(categories.get(i).name);
            if (categories.get(i).id.equals(viewModel.getText("category_id"))) selected = i + 1;
        }
        setChoices(categorySpinner, labels, selected);
        updatePublishButton();
    }

    private void setChoices(Spinner spinner, List<String> labels, int selected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selected, false);
    }

    private void chooseDate() {
        LocalDate date = LocalDate.parse(viewModel.getText("found_date"));
        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, day) ->
                viewModel.setText("found_date", LocalDate.of(year, month + 1, day).toString()),
                date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.show();
    }

    private void takePhoto() {
        try {
            File directory = new File(getFilesDir(), "photos");
            if (!directory.isDirectory() && !directory.mkdirs()) throw new IOException("Photo directory unavailable.");
            File photo = File.createTempFile("found_", ".jpg", directory);
            viewModel.setText("pending_photo", photo.getAbsolutePath());
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photo);
            camera.launch(uri);
        } catch (IOException | ActivityNotFoundException error) {
            viewModel.photoResult(false);
            Toast.makeText(this, R.string.camera_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void setFormEnabled(ViewGroup group, boolean enabled) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) setFormEnabled((ViewGroup) child, enabled);
        }
    }

    private void updatePublishButton() {
        findViewById(R.id.saveButton).setEnabled(!viewModel.isPublishing()
                && !buildings.isEmpty() && !categories.isEmpty());
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            String provider = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER
                    : manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? LocationManager.NETWORK_PROVIDER : null;
            if (provider == null) {
                viewModel.locationMessage("Enable device location or continue using the written location.");
                return;
            }
            if (locationCancellation != null) locationCancellation.cancel();
            locationCancellation = new CancellationSignal();
            viewModel.locationMessage("Getting location...");
            LocationManagerCompat.getCurrentLocation(manager, provider, locationCancellation,
                    ContextCompat.getMainExecutor(this), location -> {
                        locationCancellation = null;
                        if (location == null) viewModel.locationMessage("No location received. You can still publish using the written location.");
                        else viewModel.setCoordinates(location.getLatitude(), location.getLongitude());
                    });
        } catch (SecurityException | IllegalArgumentException error) {
            locationCancellation = null;
            viewModel.locationMessage("Location unavailable. You can still publish using the written location.");
        }
    }

    @Override protected void onStop() {
        if (locationCancellation != null) {
            locationCancellation.cancel();
            locationCancellation = null;
            viewModel.locationMessage("Location request stopped. You can request it again or use the written location.");
        }
        super.onStop();
    }
}
