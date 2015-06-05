package com.android.arthlimchiu.where;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.android.arthlimchiu.where.services.FetchAddressIntentService;
import com.android.arthlimchiu.where.services.LoadGeofencesIntentService;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhereNewPlaceFragment extends Fragment {

    private static final String TAG = "WhereNewPlaceFragment";

    public static final String ADDRESS_RECEIVER = "com.android.arthlimchiu.where.RECEIVER";
    public static final String LOCATION_EXTRA = "com.android.arthlimchiu.where.LOCATION_EXTRA";
    public static final String RESULT_DATA_KEY = "com.android.arthlimchiu.where.RESULT_DATA_KEY";
    public static final String LATITUDE_KEY = "WhereNewPlaceFragment.LATITUDE_KEY";
    public static final String LONGITUDE_KEY = "WhereNewPlaceFragment.LONGITUDE_KEY";
    public static final String ADDRESS_KEY = "WhereNewPlaceFragment.ADDRESS_KEY";
    public static final String PLACENAME_KEY = "WhereNewPlaceFragment.PLACENAME_KEY";

    public static int SUCCESS_RESULT = 0;
    public static int FAILURE_RESULT = 1;

    private EditText mPlaceNameEt;
    private TextView mAddressTv;

    Location mLocation;

    double latitude, longitude;
    String placeName, address;

    boolean canSave;

    private AddressReceiver mReceiver;

    public static WhereNewPlaceFragment newInstance(double latitude, double longitude, String placeName, String address) {
        Bundle args = new Bundle();
        args.putDouble(LATITUDE_KEY, latitude);
        args.putDouble(LONGITUDE_KEY, longitude);
        args.putString(ADDRESS_KEY, address);
        args.putString(PLACENAME_KEY, placeName);

        WhereNewPlaceFragment fragment = new WhereNewPlaceFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public WhereNewPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        canSave = false;

        mReceiver = new AddressReceiver(new Handler());

        Bundle args = getArguments();
        if (args != null) {
            latitude = args.getDouble(LATITUDE_KEY);
            longitude = args.getDouble(LONGITUDE_KEY);
            placeName = args.getString(PLACENAME_KEY, "");
            address = args.getString(ADDRESS_KEY, "");
        }

        if (TextUtils.isEmpty(address) || address.equals("")) {
            mLocation = new Location("WhereNewPlaceFragment.Location");
            mLocation.setLatitude(latitude);
            mLocation.setLongitude(longitude);
            startFetchingAddress(mLocation);
        }

        setHasOptionsMenu(true);

        getActivity().setTitle("New Place");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_where_new_place, container, false);

        mPlaceNameEt = (EditText) v.findViewById(R.id.fragment_where_new_place_placeName);
        mAddressTv = (TextView) v.findViewById(R.id.fragment_where_new_place_address);

        if (!TextUtils.isEmpty(placeName) || !placeName.equals("")) {
            mPlaceNameEt.setText(placeName);
        }

        if (!TextUtils.isEmpty(address) || !address.equals("")) {
            mAddressTv.setText(address);
            canSave = true;
        }

        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.save_changes).setVisible(canSave);
        super.onPrepareOptionsMenu(menu);
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
                if (mPlaceNameEt.getText().toString().equals("")) {
                    showDialog();
                } else {
                    saveChanges();
                    loadGeofences();
                    hideKeyboard();
                    getActivity().finish();
                    return true;
                }
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startFetchingAddress(Location location) {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        intent.putExtra(ADDRESS_RECEIVER, mReceiver);
        intent.putExtra(LOCATION_EXTRA, location);

        getActivity().startService(intent);
    }

    private void saveChanges() {
        ContentValues cv = new ContentValues();

        String placeName = mPlaceNameEt.getText().toString();
        String address = mAddressTv.getText().toString();
//        latitude = mLocation.getLatitude();
//        longitude = mLocation.getLongitude();
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

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.empty_place_name);
        builder.setPositiveButton(R.string.ok, null);

        AlertDialog dialog = builder.create();

        dialog.show();
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
            if (resultCode == FAILURE_RESULT) {
                canSave = false;
                getActivity().invalidateOptionsMenu();
            } else if (resultCode == SUCCESS_RESULT) {
                canSave = true;
                getActivity().invalidateOptionsMenu();
            }

            mAddressTv.setText(resultData.getString(RESULT_DATA_KEY));
        }
    }
}
