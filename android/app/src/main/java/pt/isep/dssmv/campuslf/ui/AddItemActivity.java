package pt.isep.dssmv.campuslf.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import pt.isep.dssmv.campuslf.R;
import pt.isep.dssmv.campuslf.model.FoundItem;

/** Formulário para registar um novo objeto encontrado. */
public final class AddItemActivity extends BaseActivity {
    private EditText titleInput;
    private EditText descriptionInput;
    private EditText locationInput;
    private Spinner categorySpinner;
    private LocalDate foundDate = LocalDate.now();
    private Button dateButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        locationInput = findViewById(R.id.locationInput);
        categorySpinner = findViewById(R.id.categorySpinner);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.item_categories,
                android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        dateButton = findViewById(R.id.dateButton);
        updateDateButton();
        dateButton.setOnClickListener(view -> openDatePicker());

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> saveItem());
        findViewById(R.id.cancelButton).setOnClickListener(view -> finish());
    }

    private void openDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    foundDate = LocalDate.of(year, month + 1, dayOfMonth);
                    updateDateButton();
                },
                foundDate.getYear(),
                foundDate.getMonthValue() - 1,
                foundDate.getDayOfMonth());

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateButton() {
        dateButton.setText(foundDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void saveItem() {
        boolean valid = validateInput(
                titleInput, "Title", 3, FoundItem.MAX_TITLE_LENGTH)
                & validateInput(
                descriptionInput, "Description", 5, FoundItem.MAX_DESCRIPTION_LENGTH)
                & validateInput(
                locationInput, "Location", 2, FoundItem.MAX_LOCATION_LENGTH);

        if (!valid) {
            return;
        }

        final FoundItem item;
        try {
            item = new FoundItem(
                    0,
                    titleInput.getText().toString(),
                    descriptionInput.getText().toString(),
                    categorySpinner.getSelectedItem().toString(),
                    locationInput.getText().toString(),
                    foundDate);
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        setFormEnabled(false);
        saveButton.setText(R.string.saving);

        runAsync(() -> repository.add(item), id -> {
            if (id == -1) {
                setFormEnabled(true);
                Toast.makeText(this, R.string.save_error, Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
            finish();
        }, exception -> {
            setFormEnabled(true);
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_LONG).show();
        });
    }

    private boolean validateInput(EditText input, String fieldName,
                                  int minLength, int maxLength) {
        try {
            FoundItem.validateText(input.getText().toString(), fieldName, minLength, maxLength);
            input.setError(null);
            return true;
        } catch (IllegalArgumentException exception) {
            input.setError(exception.getMessage());
            return false;
        }
    }

    private void setFormEnabled(boolean enabled) {
        titleInput.setEnabled(enabled);
        descriptionInput.setEnabled(enabled);
        locationInput.setEnabled(enabled);
        categorySpinner.setEnabled(enabled);
        dateButton.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        if (enabled) {
            saveButton.setText(R.string.save);
        }
    }
}
