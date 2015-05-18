package com.android.arthlimchiu.where.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.TrackTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clarence on 4/30/2015.
 */
public class NewTrackService extends IntentService {

    private static final String TAG = "NewTrackService";


    public NewTrackService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int trackId = getSharedPreferences("systemvars", MODE_PRIVATE).getInt("last_track_id", 0);

        if (trackId == 0) {
            insertNewTrack();
        } else {
            String[] projection = new String[]{TrackTable.COLUMN_DATE};
            Cursor cursor = getContentResolver().query(Uri.withAppendedPath(WhereContentProvider.CONTENT_URI_TRACKS,
                    String.valueOf(trackId)), projection, null, null, null);

            if (cursor.moveToFirst()) {

                Calendar currentTrackDate = Calendar.getInstance();
                Calendar nowDate = Calendar.getInstance();

                currentTrackDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TrackTable.COLUMN_DATE)));

                String currentTrackYear = String.valueOf(currentTrackDate.get(Calendar.YEAR));
                String currentTrackMonth = String.valueOf(currentTrackDate.get(Calendar.MONTH));
                String currentTrackDay = String.valueOf(currentTrackDate.get(Calendar.DAY_OF_MONTH));

                String nowYear = String.valueOf(nowDate.get(Calendar.YEAR));
                String nowMonth = String.valueOf(nowDate.get(Calendar.MONTH));
                String nowDay = String.valueOf(nowDate.get(Calendar.DAY_OF_MONTH));

                Log.i(TAG, "Comparing dates");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date currentTrack = sdf.parse(currentTrackYear + "-" + currentTrackMonth + "-" + currentTrackDay);
                    Date now = sdf.parse(nowYear + "-" + nowMonth + "-" + nowDay);

                    if (now.after(currentTrack)) {
                        insertNewTrack();
                    }

                } catch (ParseException e) {
                    Log.e(TAG, e.toString());
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertNewTrack() {
        ContentValues cv = new ContentValues();
        cv.put(TrackTable.COLUMN_DATE, Calendar.getInstance().getTimeInMillis());

        Uri uri = getContentResolver().insert(WhereContentProvider.CONTENT_URI_TRACKS, cv);
        getSharedPreferences("systemvars", MODE_PRIVATE).edit().putInt("last_track_id", Integer.parseInt(uri.getLastPathSegment())).commit();
        Log.i(TAG, "Service started, new track no. " + getSharedPreferences("systemvars", MODE_PRIVATE).getInt("last_track_id", 0));
    }
}
