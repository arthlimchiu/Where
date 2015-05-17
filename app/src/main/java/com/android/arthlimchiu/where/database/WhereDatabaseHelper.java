package com.android.arthlimchiu.where.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clarence on 4/24/2015.
 */
public class WhereDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wheredb.db";
    private static final int DATABASE_VERSION = 1;

    public WhereDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        WhereTable.onCreate(db);
        TrackTable.onCreate(db);
        PlaceTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO pass statements here for upgrading database
    }
}
