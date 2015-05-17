package com.android.arthlimchiu.where;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.android.arthlimchiu.where.services.FetchAddressIntentService;
import com.android.arthlimchiu.where.services.LoadGeofencesIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhereNewPlaceFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final String TAG = "WhereNewPlaceFragment";

    public static final String ADDRESS_RECEIVER = "com.android.arthlimchiu.where.RECEIVER";
    public static final String LOCATION_EXTRA = "com.android.arthlimchiu.where.LOCATION_EXTRA";
    public static final String RESULT_DATA_KEY = "com.android.arthlimchiu.where.RESULT_DATA_KEY";

    public static int SUCCESS_RESULT = 0;
    public static int FAILURE_RESULT = 1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private EditText mPlaceNameEt;
    private TextView mAddressTv;
    private Button mChngLocBtn;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    double latitude, longitude;

    private AddressReceiver mReceiver;

    public WhereNewPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new AddressReceiver(new Handler());

        setHasOptionsMenu(true);

        getActivity().setTitle("New Place");

        buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_where_new_place, container, false);

        mPlaceNameEt = (EditText) v.findViewById(R.id.fragment_where_new_place_placeName);
        mAddressTv = (TextView) v.findViewById(R.id.fragment_where_new_place_address);
        mChngLocBtn = (Button) v.findViewById(R.id.fragment_where_new_place_location);
        mChngLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WhereMapActivity.class);
                intent.putExtra(WhereMapFragment.KEY_MAP_VIEW, WhereMapFragment.MAP_BY_LATLNG);
                intent.putExtra(WhereMapFragment.KEY_PLACE_LATITUDE, mCurrentLocation.getLatitude());
                intent.putExtra(WhereMapFragment.KEY_PLACE_LONGITUDE, mCurrentLocation.getLongitude());
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_where_new_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_changes:
                saveChanges();
                loadGeofences();
                hideKeyboard();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        startLocationUpdates();
        Log.i(TAG, "Connected, starting location updates");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
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

        intent.putExtra(ADDRESS_RECEIVER, mReceiver);
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

    private void saveChanges() {
        ContentValues cv = new ContentValues();

        String placeName = mPlaceNameEt.getText().toString();
        String address = mAddressTv.getText().toString();
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
        long date = new Date().getTime();

        cv.put(PlaceTable.COLUMN_PLACE_NAME, placeName);
        cv.put(PlaceTable.COLUMN_ADDRESS, address);
        cv.put(PlaceTable.COLUMN_DATE, date);
        cv.put(PlaceTable.COLUMN_LATITUDE, latitude);
        cv.put(PlaceTable.COLUMN_LONGITUDE, longitude);

        getActivity().getContentResolver().insert(WhereContentProvider.CONTENT_URI_PLACES, cv);
    }

    private void loadGeofences() {
        Intent intent = new Intent(getActivity(), LoadGeofencesIntentService.class);
        getActivity().startService(intent);
    }

    private void hideKeyboard() {
        InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ime.hideSoftInputFromWindow(mPlaceNameEt.getWindowToken(), 0);
    }

    class AddressReceiver extends ResultReceiver {

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressTv.setText(resultData.getString(RESULT_DATA_KEY));
        }
    }
}
