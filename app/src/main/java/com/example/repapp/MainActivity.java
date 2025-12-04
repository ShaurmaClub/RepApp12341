package com.example.repapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView dateTimeTextView;
    private Button createRequestButton, createPaintRequestButton, statisticsButton;
    private ListView repairRequestsListView, paintRequestsListView;
    private DatabaseHelper dbHelper;
    private String repairSortOrder = null;
    private String paintSortOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        createRequestButton = findViewById(R.id.createRequestButton);
        createPaintRequestButton = findViewById(R.id.createPaintRequestButton);
        statisticsButton = findViewById(R.id.statisticsButton);
        repairRequestsListView = findViewById(R.id.repairRequestsListView);
        paintRequestsListView = findViewById(R.id.paintRequestsListView);

        ImageButton sortRepairAlphabeticallyButton = findViewById(R.id.sortRepairAlphabeticallyButton);
        ImageButton sortRepairByDateButton = findViewById(R.id.sortRepairByDateButton);
        ImageButton sortPaintAlphabeticallyButton = findViewById(R.id.sortPaintAlphabeticallyButton);
        ImageButton sortPaintByDateButton = findViewById(R.id.sortPaintByDateButton);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        dateTimeTextView.setText(currentDateandTime);

        createRequestButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateRequestActivity.class)));
        createPaintRequestButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreatePaintRequestActivity.class)));
        statisticsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StatisticsActivity.class)));

        sortRepairAlphabeticallyButton.setOnClickListener(v -> {
            repairSortOrder = DatabaseHelper.COLUMN_MODEL + " ASC";
            loadRepairRequests();
        });

        sortRepairByDateButton.setOnClickListener(v -> {
            repairSortOrder = DatabaseHelper.COLUMN_DATE + " DESC";
            loadRepairRequests();
        });

        sortPaintAlphabeticallyButton.setOnClickListener(v -> {
            paintSortOrder = DatabaseHelper.COLUMN_MODEL + " ASC";
            loadPaintRequests();
        });

        sortPaintByDateButton.setOnClickListener(v -> {
            paintSortOrder = DatabaseHelper.COLUMN_DATE + " DESC";
            loadPaintRequests();
        });

        loadRepairRequests();
        loadPaintRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepairRequests();
        loadPaintRequests();
    }

    private void loadRepairRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_REPAIR_REQUESTS,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_PHONE, DatabaseHelper.COLUMN_MODEL, DatabaseHelper.COLUMN_DATE},
                null, null, null, null, repairSortOrder);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item_repair,
                cursor, new String[]{DatabaseHelper.COLUMN_PHONE, DatabaseHelper.COLUMN_MODEL, DatabaseHelper.COLUMN_DATE},
                new int[]{R.id.phoneTextView, R.id.modelTextView, R.id.dateTextView}, 0);

        repairRequestsListView.setAdapter(adapter);
    }

    private void loadPaintRequests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PAINT_REQUESTS,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_PHONE, DatabaseHelper.COLUMN_COLOR, DatabaseHelper.COLUMN_DATE},
                null, null, null, null, paintSortOrder);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item_paint,
                cursor, new String[]{DatabaseHelper.COLUMN_PHONE, DatabaseHelper.COLUMN_COLOR, DatabaseHelper.COLUMN_DATE},
                new int[]{R.id.phoneTextView, R.id.colorTextView, R.id.dateTextView}, 0);

        paintRequestsListView.setAdapter(adapter);
    }
}
