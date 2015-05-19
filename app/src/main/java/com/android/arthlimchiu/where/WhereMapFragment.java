package com.android.arthlimchiu.where;


import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class WhereMapFragment extends SupportMapFragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMapClickListener {

    private static int MAP_LOADER = 3;

    public static final String KEY_PLACE_ID = "KEY_PLACE_ID";
    public static final String KEY_PLACE_LATITUDE = "KEY_PLACE_LATITUDE";
    public static final String KEY_PLACE_LONGITUDE = "KEY_PLACE_LONGITUDE";
    public static final String KEY_MAP_VIEW = "KEY_MAP_VIEW";

    public static int MAP_BY_ID = 0;
    public static int MAP_BY_LATLNG = 1;
    public static int MAP_ALL = 2;

    private GoogleMap mGoogleMap;

    private int viewMapBy;
    private long id = -1;
    private double latitude, longitude;

    public WhereMapFragment() {
        // Required empty public constructor
    }

    public static WhereMapFragment newInstance(long id, int viewMapBy) {
        Bundle args = new Bundle();
        args.putLong(KEY_PLACE_ID, id);
        args.putInt(KEY_MAP_VIEW, viewMapBy);
        WhereMapFragment fragment = new WhereMapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static WhereMapFragment newInstance(double latitude, double longitude, int viewMapBy) {
        Bundle args = new Bundle();
        args.putDouble(KEY_PLACE_LATITUDE, latitude);
        args.putDouble(KEY_PLACE_LONGITUDE, longitude);
        args.putInt(KEY_MAP_VIEW, viewMapBy);

        WhereMapFragment fragment = new WhereMapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
        Bundle args = getArguments();
        if (args != null) {
            id = args.getLong(KEY_PLACE_ID, -1);
            latitude = args.getDouble(KEY_PLACE_LATITUDE);
            longitude = args.getDouble(KEY_PLACE_LONGITUDE);
            viewMapBy = args.getInt(KEY_MAP_VIEW);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLoaderManager().initLoader(MAP_LOADER, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{PlaceTable.COLUMN_ID, PlaceTable.COLUMN_PLACE_NAME, PlaceTable.COLUMN_LATITUDE, PlaceTable.COLUMN_LONGITUDE};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), WhereContentProvider.CONTENT_URI_PLACES, projection, null, null, "date desc");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        LatLng latLng;

        while (data.moveToNext()) {
            latLng = new LatLng(data.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LATITUDE)), data.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LONGITUDE)));

            addMarker(latLng, data.getString(data.getColumnIndex(PlaceTable.COLUMN_PLACE_NAME)));
        }

        if (viewMapBy == MAP_BY_ID) {
            String[] projection = {PlaceTable.COLUMN_LATITUDE, PlaceTable.COLUMN_LONGITUDE};
            Cursor cursor = getActivity().getContentResolver()
                    .query(Uri.withAppendedPath(WhereContentProvider.CONTENT_URI_PLACES, String.valueOf(id)), projection, null, null, null);
            latLng = new LatLng(cursor.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LATITUDE)), cursor.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LONGITUDE)));
            cursor.close();

            moveCamera(latLng);
        } else if (viewMapBy == MAP_BY_LATLNG) {
            latLng = new LatLng(latitude, longitude);

            moveCamera(latLng);
        } else if (data.moveToFirst()) {
            latLng = new LatLng(data.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LATITUDE)), data.getDouble(data.getColumnIndex(PlaceTable.COLUMN_LONGITUDE)));

            moveCamera(latLng);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addMarker(LatLng latLng, String title) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(title);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.strokeColor(0xFF0000FF);
        circleOptions.strokeWidth(2);
        circleOptions.fillColor(0x110000FF);
        circleOptions.radius(100);
        mGoogleMap.addCircle(circleOptions);
        mGoogleMap.addMarker(options);
    }

    private void moveCamera(LatLng latLng) {
        CameraUpdate movement = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mGoogleMap.animateCamera(movement);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
