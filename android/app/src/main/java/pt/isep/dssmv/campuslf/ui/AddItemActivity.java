package com.campuslf.app.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.campuslf.app.R;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Formulário para registar um novo objeto encontrado. */
public final class AddItemActivity extends Activity {
    private LocalDate foundDate = LocalDate.now();
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.item_categories,
                android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        dateButton = findViewById(R.id.dateButton);
        updateDateButton();
        dateButton.setOnClickListener(view -> openDatePicker());

        findViewById(R.id.saveButton).setOnClickListener(view ->
                Toast.makeText(this, R.string.save_not_ready, Toast.LENGTH_SHORT).show());
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
}
