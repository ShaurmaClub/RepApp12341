package com.example.repapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatisticsCursorAdapter extends CursorAdapter {

    private DatabaseHelper dbHelper;
    private OnDataSetChangedListener onDataSetChangedListener;

    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    public void setOnDataSetChangedListener(OnDataSetChangedListener listener) {
        this.onDataSetChangedListener = listener;
    }

    public StatisticsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_statistics, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView modelTextView = view.findViewById(R.id.modelTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView statusTextView = view.findViewById(R.id.statusTextView);
        ImageView editButton = view.findViewById(R.id.editButton);
        ImageView completeButton = view.findViewById(R.id.completeButton);
        ImageView deleteButton = view.findViewById(R.id.deleteButton);

        String model = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MODEL));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));

        modelTextView.setText(model);
        dateTextView.setText(date);
        statusTextView.setText(status);

        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        final String tableName = cursor.getString(cursor.getColumnIndexOrThrow("tableName"));

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRequestActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("tableName", tableName);
            context.startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(tableName, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            if (onDataSetChangedListener != null) {
                onDataSetChangedListener.onDataSetChanged();
            }
        });

        completeButton.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(DatabaseHelper.COLUMN_STATUS, "выполнено");
            db.update(tableName, values, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            if (onDataSetChangedListener != null) {
                onDataSetChangedListener.onDataSetChanged();
            }
        });

    }
}
