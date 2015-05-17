package com.android.arthlimchiu.where.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Clarence on 4/30/2015.
 */
public class TrackTable {

    // Database Table
    public static final String TABLE_TRACK = "tracks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRACK
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " integer"
            + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO Upgrading database statements here
    }
}
