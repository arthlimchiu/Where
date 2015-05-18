package com.android.arthlimchiu.where.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.android.arthlimchiu.where.database.WhereTable;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clarence on 5/6/2015.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Geofence Event triggered");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);

            return;
        }

        int lastTrackId = getSharedPreferences("systemvars", MODE_PRIVATE).getInt("last_track_id", 1);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "Geofence enter event triggered");
            String[] projection = new String[]{PlaceTable.COLUMN_PLACE_NAME};
            Cursor cursor = null;

            for (Geofence geofence : triggeringGeofences) {

                if (getWhereStatus(lastTrackId, geofence.getRequestId()) == 0 || getWhereStatus(lastTrackId, geofence.getRequestId()) == -1) {
                    cursor = getContentResolver().query(Uri.withAppendedPath(WhereContentProvider.CONTENT_URI_PLACES, geofence.getRequestId()), projection, null, null, null);
                    long timeIn = Calendar.getInstance().getTimeInMillis();

                    if (cursor.moveToFirst()) {

                        String contentText = "Arrived: " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timeIn);
                        generateNotification("You have entered a place", cursor.getString(cursor.getColumnIndex(PlaceTable.COLUMN_PLACE_NAME)), contentText);

                        ContentValues cv = new ContentValues();
                        cv.put(WhereTable.COLUMN_TRACK_ID, lastTrackId);
                        cv.put(WhereTable.COLUMN_PLACE_ID, Integer.parseInt(geofence.getRequestId()));
                        cv.put(WhereTable.COLUMN_TIME_IN, timeIn);
                        cv.put(WhereTable.COLUMN_STATUS, 1);
                        Uri uri = getContentResolver().insert(WhereContentProvider.CONTENT_URI_WHERES, cv);
                        getSharedPreferences("systemvars", MODE_PRIVATE).edit().putInt("current_where_id", Integer.parseInt(uri.getLastPathSegment())).commit();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Geofence exit event triggered");
            String[] projection = new String[]{PlaceTable.COLUMN_PLACE_NAME};
            Cursor cursor = null;

            for (Geofence geofence : triggeringGeofences) {

                if (getWhereStatus(lastTrackId, geofence.getRequestId()) == 1) {
                    cursor = getContentResolver().query(Uri.withAppendedPath(WhereContentProvider.CONTENT_URI_PLACES, geofence.getRequestId()), projection, null, null, null);
                    long timeOut = Calendar.getInstance().getTimeInMillis();
                    int currentWhereId = getSharedPreferences("systemvars", MODE_PRIVATE).getInt("current_where_id", 1);

                    if (cursor.moveToFirst()) {

                        String contentText = "Left: " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTimeInMillis());
                        generateNotification("You have left a place", cursor.getString(cursor.getColumnIndex(PlaceTable.COLUMN_PLACE_NAME)), contentText);

                        ContentValues cv = new ContentValues();
                        cv.put(WhereTable.COLUMN_TIME_OUT, timeOut);
                        cv.put(WhereTable.COLUMN_STATUS, 2);

                        getContentResolver().update(Uri.withAppendedPath(WhereContentProvider.CONTENT_URI_WHERES, String.valueOf(currentWhereId)), cv, null, null);
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    private int getWhereStatus(int trackId, String requestId) {

        String[] whereProjection = new String[]{WhereTable.COLUMN_STATUS};
        String whereSelection = WhereTable.COLUMN_TRACK_ID + "=? AND " +
                WhereTable.COLUMN_PLACE_ID +
                "=" +
                requestId;
        String[] whereSelectionArgs = new String[]{String.valueOf(trackId)};
        Cursor whereCursor = getContentResolver().query(WhereContentProvider.CONTENT_URI_WHERES, whereProjection, whereSelection, whereSelectionArgs, null);

        if (whereCursor.moveToFirst()) {
            int status = whereCursor.getInt(whereCursor.getColumnIndex(WhereTable.COLUMN_STATUS));
            whereCursor.close();

            return status;
        }

        return -1;
    }

    private void generateNotification(String tickerText, String contentTitle, String contentText) {
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(tickerText)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    public String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available.";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences.";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents.";
            default:
                return "Unknown error.";
        }
    }
}
