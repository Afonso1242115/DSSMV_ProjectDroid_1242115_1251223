package com.campuslf.app.ui;
import pt.isep.dssmv.campuslf.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.campuslf.app.data.FoundItemRepository;
import com.campuslf.app.model.FoundItem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Formulário para registar um novo objeto encontrado. */
public final class AddItemActivity extends Activity {
    private EditText titleInput;
    private EditText descriptionInput;
    private EditText locationInput;
    private Spinner categorySpinner;
    private LocalDate foundDate = LocalDate.now();
    private Button dateButton;
    private FoundItemRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        repository = new FoundItemRepository(this);
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

        findViewById(R.id.saveButton).setOnClickListener(view -> saveItem());
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

        try {
            FoundItem item = new FoundItem(
                    0,
                    titleInput.getText().toString(),
                    descriptionInput.getText().toString(),
                    categorySpinner.getSelectedItem().toString(),
                    locationInput.getText().toString(),
                    foundDate);

            long id = repository.add(item);
            if (id == -1) {
                Toast.makeText(this, R.string.save_error, Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
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
}