package com.android.arthlimchiu.where;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.arthlimchiu.where.services.FetchAddressIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhereCreateFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final String TAG = "WhereCreateFragment";

    public static final String ADDRESS_RECEIVER = "com.android.arthlimchiu.where.RECEIVER";
    public static final String LOCATION_EXTRA = "com.android.arthlimchiu.where.LOCATION_EXTRA";
    public static final String RESULT_DATA_KEY = "com.android.arthlimchiu.where.RESULT_DATA_KEY";

    public static int SUCCESS_RESULT = 0;
    public static int FAILURE_RESULT = 1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    TextView mLatitude, mLongitude, mAddress, mPlaceName;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    AddressReceiver mAddressReceiver;


    public WhereCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAddressReceiver = new AddressReceiver(new Handler());

        setHasOptionsMenu(true);

        buildGoogleApiClient();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_where_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_changes:
                saveChanges();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveChanges() {
        String placeName = mPlaceName.getText().toString();
        String address = mAddress.getText().toString();
        double latitude = Double.parseDouble(mLatitude.getText().toString());
        double longitude = Double.parseDouble(mLongitude.getText().toString());

//        ContentValues cv = new ContentValues();
//        cv.put(WhereTable.COLUMN_PLACE_NAME, placeName);
//        cv.put(WhereTable.COLUMN_ADDRESS, address);
//        cv.put(WhereTable.COLUMN_LATITUDE, latitude);
//        cv.put(WhereTable.COLUMN_LONGITUDE, longitude);
//
//        getActivity().getContentResolver().insert(WhereContentProvider.CONTENT_URI, cv);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_where_create, container, false);
        mLatitude = (TextView) v.findViewById(R.id.fragment_where_create_latitude);
        mLongitude = (TextView) v.findViewById(R.id.fragment_where_create_longitude);
        mAddress = (TextView) v.findViewById(R.id.fragment_where_create_addressDesc);
        mPlaceName = (TextView) v.findViewById(R.id.fragment_where_create_placeNameDesc);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "GooglApiClient is connected");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Connection suspended");

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLatitude.setText("" + mCurrentLocation.getLatitude());
        mLongitude.setText("" + mCurrentLocation.getLongitude());
        startFetchingAddress();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startFetchingAddress() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        intent.putExtra(ADDRESS_RECEIVER, mAddressReceiver);
        intent.putExtra(LOCATION_EXTRA, mCurrentLocation);

        getActivity().startService(intent);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    class AddressReceiver extends ResultReceiver {

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            if (resultCode == SUCCESS_RESULT) {
//                mAddress.setText(resultData.getString(RESULT_DATA_KEY));
//            } else {
//                mAddress.setText(resultData.getString(RESULT_DATA_KEY));
//            }

            mAddress.setText(resultData.getString(RESULT_DATA_KEY));
        }
    }
}
