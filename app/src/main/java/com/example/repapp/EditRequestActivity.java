package com.example.repapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
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

public class EditRequestActivity extends AppCompatActivity {

    private EditText ownerNameEditText, phoneEditText, modelEditText, problemEditText, colorEditText;
    private Button saveButton, datePickerButton, timePickerButton;
    private DatabaseHelper dbHelper;
    private long requestId;
    private String tableName;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        dbHelper = new DatabaseHelper(this);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Редактирование заявки");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        modelEditText = findViewById(R.id.modelEditText);
        problemEditText = findViewById(R.id.problemEditText);
        colorEditText = findViewById(R.id.colorEditText);
        saveButton = findViewById(R.id.saveButton);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);

        requestId = getIntent().getLongExtra("id", -1);
        tableName = getIntent().getStringExtra("tableName");

        if (requestId == -1) {
            Toast.makeText(this, "Ошибка: неверный ID заявки", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRequestData();

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRequest();
            }
        });
    }

    private void loadRequestData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, null, DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(requestId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String ownerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
            String model = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MODEL));

            ownerNameEditText.setText(ownerName);
            phoneEditText.setText(phone);
            modelEditText.setText(model);

            if (tableName.equals(DatabaseHelper.TABLE_REPAIR_REQUESTS)) {
                String problem = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROBLEM));
                problemEditText.setText(problem);
                colorEditText.setVisibility(View.GONE);
            } else if (tableName.equals(DatabaseHelper.TABLE_PAINT_REQUESTS)) {
                String color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COLOR));
                colorEditText.setText(color);
                problemEditText.setVisibility(View.GONE);
            }
            cursor.close();
        }
    }

    private void saveRequest() {
        String ownerName = ownerNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OWNER_NAME, ownerName);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_MODEL, model);

        String date = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute;
        values.put(DatabaseHelper.COLUMN_DATE, date);

        if (tableName.equals(DatabaseHelper.TABLE_REPAIR_REQUESTS)) {
            String problem = problemEditText.getText().toString().trim();
            if (problem.isEmpty()) {
                Toast.makeText(this, "Описание проблемы не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            values.put(DatabaseHelper.COLUMN_PROBLEM, problem);
        } else if (tableName.equals(DatabaseHelper.TABLE_PAINT_REQUESTS)) {
            String color = colorEditText.getText().toString().trim();
            if (color.isEmpty()) {
                Toast.makeText(this, "Цвет не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            values.put(DatabaseHelper.COLUMN_COLOR, color);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.update(tableName, values, DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(requestId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Заявка успешно обновлена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при обновлении заявки", Toast.LENGTH_SHORT).show();
        }
    }
}
