package com.android.arthlimchiu.where.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Clarence on 5/6/2015.
 */
public class LoadGeofencesIntentService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = "LoadGeofencesIS";

    private GoogleApiClient mGoogleApiClient;

    private PendingIntent mGeofencePendingIntent;

    private ArrayList<Geofence> mGeofences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGeofences = new ArrayList<Geofence>();

        buildGoogleApiClient();

        populateGeofences();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (mGeofences.size() > 0) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);
        }
        Log.i(TAG, "Connected, adding geofences");
        stopSelf();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed");
    }

    @Override
    public void onResult(Status status) {

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void populateGeofences() {
        String[] projection = new String[]{PlaceTable.COLUMN_ID, PlaceTable.COLUMN_LATITUDE, PlaceTable.COLUMN_LONGITUDE};
        Cursor cursor = getContentResolver().query(WhereContentProvider.CONTENT_URI_PLACES, projection, null, null, null);

        while (cursor.moveToNext()) {

            Log.i(TAG, String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaceTable.COLUMN_ID))) + ": " +
                    cursor.getDouble(cursor.getColumnIndex(PlaceTable.COLUMN_LATITUDE)) + ", " +
                    cursor.getDouble(cursor.getColumnIndex(PlaceTable.COLUMN_LONGITUDE)));
            mGeofences.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaceTable.COLUMN_ID))))
                    .setCircularRegion(
                            cursor.getDouble(cursor.getColumnIndex(PlaceTable.COLUMN_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(PlaceTable.COLUMN_LONGITUDE)),
                            100
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        cursor.close();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofences);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
