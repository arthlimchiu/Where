package com.android.arthlimchiu.where.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.android.arthlimchiu.where.WhereNewPlaceFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clarence on 4/23/2015.
 */
public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "com.android.arthlimchiu.where.services.FetchAddressIntentService";

    private ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(WhereNewPlaceFragment.ADDRESS_RECEIVER);

        if (mReceiver == null) {
            Log.e("FetchAddressService", "No receiver received. There is nowhere to send results");
            return;
        }

        Location location = intent.getParcelableExtra(WhereNewPlaceFragment.LOCATION_EXTRA);


        if (location == null) {
            errorMessage = "No location data provided.";
            Log.e("FetchAddressService", errorMessage);
            deliverResultToReceiver(WhereNewPlaceFragment.FAILURE_RESULT, errorMessage);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            errorMessage = "Please check your internet connection.";
            Log.e("FetchAddressService", errorMessage, e);
        } catch (IllegalArgumentException e) {
            errorMessage = "Invalid latitude or longitude value.";
            Log.e("FetchAddressService", errorMessage + ". " + "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), e);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found.";
                Log.e("FetchAddressService", errorMessage);
            }
            deliverResultToReceiver(WhereNewPlaceFragment.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i("FetchAddressService", "Address found.");
            deliverResultToReceiver(WhereNewPlaceFragment.SUCCESS_RESULT, TextUtils.join(", ", addressFragments));
        }

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(WhereNewPlaceFragment.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
