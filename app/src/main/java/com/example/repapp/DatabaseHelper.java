package com.example.repapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "requests.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_REPAIR_REQUESTS = "repair_requests";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_OWNER_NAME = "owner_name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_PROBLEM = "problem";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "status";

    public static final String TABLE_PAINT_REQUESTS = "paint_requests";
    public static final String COLUMN_COLOR = "color";

    private static final String TABLE_CREATE_REPAIR = "CREATE TABLE "
            + TABLE_REPAIR_REQUESTS + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OWNER_NAME
            + " TEXT, " + COLUMN_PHONE + " TEXT, " + COLUMN_MODEL
            + " TEXT, " + COLUMN_PROBLEM + " TEXT, " + COLUMN_DATE + " TEXT, "
            + COLUMN_STATUS + " TEXT);";

    private static final String TABLE_CREATE_PAINT = "CREATE TABLE "
            + TABLE_PAINT_REQUESTS + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OWNER_NAME
            + " TEXT, " + COLUMN_PHONE + " TEXT, " + COLUMN_MODEL
            + " TEXT, " + COLUMN_COLOR + " TEXT, " + COLUMN_DATE + " TEXT, "
            + COLUMN_STATUS + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_REPAIR);
        db.execSQL(TABLE_CREATE_PAINT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPAIR_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAINT_REQUESTS);
        onCreate(db);
    }
}
