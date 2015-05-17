package com.android.arthlimchiu.where.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Clarence on 4/24/2015.
 */
public class WhereTable {

    // Database table
    public static final String TABLE_WHERE = "wheres";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRACK_ID = "track_id";
    public static final String COLUMN_PLACE_ID = "place_id";
    public static final String COLUMN_TIME_IN = "time_in";
    public static final String COLUMN_TIME_OUT = "time_out";
    public static final String COLUMN_TIME_STAYED = "time_stayed";
    public static final String COLUMN_STATUS = "status";

    // Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_WHERE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TRACK_ID + " integer not null, "
            + COLUMN_PLACE_ID + " integer not null, "
            + COLUMN_TIME_IN + " integer, "
            + COLUMN_TIME_OUT + " integer, "
            + COLUMN_TIME_STAYED + " integer, "
            + COLUMN_STATUS + " integer default 0, "
            + "FOREIGN KEY(" + COLUMN_TRACK_ID + ") REFERENCES " + TrackTable.TABLE_TRACK + "(" + TrackTable.COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_PLACE_ID + ") REFERENCES " + PlaceTable.TABLE_PLACE + "(" + PlaceTable.COLUMN_ID + ")"
            + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // todo statements for upgrading the database
    }

}


//    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_PLACE_NAME = "place_name";
//    public static final String COLUMN_ADDRESS = "address";
//    public static final String COLUMN_LATITUDE = "latitude";
//    public static final String COLUMN_LONGITUDE = "longitude";
//private static final String DATABASE_CREATE = "create table "
//        + TABLE_WHERE
//        + "("
//        + COLUMN_ID + " integer primary key autoincrement, "
//        + COLUMN_PLACE_NAME + " text not null, "
//        + COLUMN_ADDRESS + " text not null, "
//        + COLUMN_LATITUDE + " real not null, "
//        + COLUMN_LONGITUDE + " real not null"
//        + ")";