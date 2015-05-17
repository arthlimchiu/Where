package com.android.arthlimchiu.where.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.TrackTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clarence on 4/30/2015.
 */
public class NewTrackService extends IntentService {

    private static final String TAG = "com.android.arthlimchiu.where.services.NewTrackService";

    public NewTrackService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Date date = new Date();
        ContentValues cv = new ContentValues();
        cv.put(TrackTable.COLUMN_DATE, Calendar.getInstance().getTimeInMillis());


        Uri uri = getContentResolver().insert(WhereContentProvider.CONTENT_URI_TRACKS, cv);
        getSharedPreferences("systemvars", MODE_PRIVATE).edit().putInt("last_track_id", Integer.parseInt(uri.getLastPathSegment())).commit();
        Log.i("NewTrackService", "Service started, new track no. " + getSharedPreferences("systemvars", MODE_PRIVATE).getInt("last_track_id", 0));
    }
}
