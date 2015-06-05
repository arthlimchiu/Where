package com.android.arthlimchiu.where;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class WherePlaceSearchFragment extends Fragment {

    private static final String TAG = "PlaceSearchFragment";

    private static final int REQUEST_PLACE_PICKER = 1;

    private Button mFindNearbyBtn, mFindMapBtn;

    public WherePlaceSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("New Place");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_where_place_search, container, false);

        mFindNearbyBtn = (Button) v.findViewById(R.id.fragment_where_place_search_nearby);
        mFindNearbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getActivity());

                    startActivityForResult(intent, REQUEST_PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "Google Play Services is not available.", Toast.LENGTH_LONG).show();
                }
            }
        });
        mFindMapBtn = (Button) v.findViewById(R.id.fragment_where_place_search_map);
        mFindMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhereMapFragment fragment = new WhereMapFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_where_new_place_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(data, getActivity());
            LatLng latLng = place.getLatLng();

            WhereNewPlaceFragment fragment = WhereNewPlaceFragment.newInstance(latLng.latitude, latLng.longitude, place.getName().toString(), place.getAddress().toString());

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragment_where_new_place_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
