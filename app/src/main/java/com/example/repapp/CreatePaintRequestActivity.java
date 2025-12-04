package com.example.repapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreatePaintRequestActivity extends AppCompatActivity {

    private EditText ownerNameEditText, phoneEditText, modelEditText, colorEditText;
    private Button datePickerButton, timePickerButton, submitButton;
    private DatabaseHelper dbHelper;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_paint_request);

        dbHelper = new DatabaseHelper(this);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Создание заявки на покраску");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        modelEditText = findViewById(R.id.modelEditText);
        colorEditText = findViewById(R.id.colorEditText);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);
        submitButton = findViewById(R.id.submitButton);

        datePickerButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        this.year = year;
                        this.month = monthOfYear;
                        this.day = dayOfMonth;
                    }, year, month, day);
            datePickerDialog.show();
        });

        timePickerButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        this.hour = hourOfDay;
                        this.minute = minute;
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRequest();
            }
        });
    }

    private void saveRequest() {
        String ownerName = ownerNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();

        String date = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute;

        if (ownerName.isEmpty() || phone.isEmpty() || model.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OWNER_NAME, ownerName);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_MODEL, model);
        values.put(DatabaseHelper.COLUMN_COLOR, color);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_STATUS, "в работе");

        long newRowId = db.insert(DatabaseHelper.TABLE_PAINT_REQUESTS, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Заявка успешно создана", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при создании заявки", Toast.LENGTH_SHORT).show();
        }
    }
}
