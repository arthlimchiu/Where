package com.android.arthlimchiu.where.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Clarence on 4/30/2015.
 */
public class PlaceTable {

    // Database Table
    public static final String TABLE_PLACE = "places";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLACE_NAME = "place_name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PLACE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PLACE_NAME + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_DATE + " integer, "
            + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real"
            + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        // TODO Upgrading database statements here
    }
}
