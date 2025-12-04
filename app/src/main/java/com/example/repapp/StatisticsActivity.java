package com.example.repapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity implements StatisticsCursorAdapter.OnDataSetChangedListener {

    private ListView statisticsListView;
    private TextView repairRequestsCountTextView, paintRequestsCountTextView, completedRequestsTextView;
    private DatabaseHelper dbHelper;
    private StatisticsCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Статистика");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        statisticsListView = findViewById(R.id.statisticsListView);
        repairRequestsCountTextView = findViewById(R.id.repairRequestsCountTextView);
        paintRequestsCountTextView = findViewById(R.id.paintRequestsCountTextView);
        completedRequestsTextView = findViewById(R.id.completedRequestsTextView);

        loadStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void loadStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor repairCursor = db.rawQuery("SELECT *, '" + DatabaseHelper.TABLE_REPAIR_REQUESTS + "' AS tableName FROM " + DatabaseHelper.TABLE_REPAIR_REQUESTS, null);
        Cursor paintCursor = db.rawQuery("SELECT *, '" + DatabaseHelper.TABLE_PAINT_REQUESTS + "' AS tableName FROM " + DatabaseHelper.TABLE_PAINT_REQUESTS, null);

        int repairRequestsCount = repairCursor.getCount();
        int paintRequestsCount = paintCursor.getCount();

        int completedRequests = 0;

        if (repairCursor.moveToFirst()) {
            do {
                if ("выполнено".equals(repairCursor.getString(repairCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)))) {
                    completedRequests++;
                }
            } while (repairCursor.moveToNext());
        }

        if (paintCursor.moveToFirst()) {
            do {
                if ("выполнено".equals(paintCursor.getString(paintCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)))) {
                    completedRequests++;
                }
            } while (paintCursor.moveToNext());
        }

        repairRequestsCountTextView.setText("Количество заявок на ремонт: " + repairRequestsCount);
        paintRequestsCountTextView.setText("Количество заявок на покраску: " + paintRequestsCount);
        completedRequestsTextView.setText("Количество выполненных заявок: " + completedRequests);

        // Move cursors to the beginning before creating MergeCursor
        repairCursor.moveToPosition(-1);
        paintCursor.moveToPosition(-1);

        Cursor mergedCursor = new MergeCursor(new Cursor[]{repairCursor, paintCursor});
        adapter = new StatisticsCursorAdapter(this, mergedCursor);
        adapter.setOnDataSetChangedListener(this);
        statisticsListView.setAdapter(adapter);
    }

    @Override
    public void onDataSetChanged() {
        loadStatistics();
    }
}
