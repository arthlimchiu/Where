package com.android.arthlimchiu.where;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class WhereMapActivity extends ActionBarActivity {

    private long id;
    private double latitude, longitude;
    private int viewMapBy = WhereMapFragment.MAP_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_map);

        Intent intent = getIntent();
        viewMapBy = intent.getIntExtra(WhereMapFragment.KEY_MAP_VIEW, WhereMapFragment.MAP_ALL);
        id = intent.getLongExtra(WhereMapFragment.KEY_PLACE_ID, -1);
        latitude = intent.getDoubleExtra(WhereMapFragment.KEY_PLACE_LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(WhereMapFragment.KEY_PLACE_LONGITUDE, 0.0);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_where_map_container);

        if (fragment == null) {
            if (viewMapBy == WhereMapFragment.MAP_BY_ID) {
                fragment = WhereMapFragment.newInstance(id, viewMapBy, false);
            } else if (viewMapBy == WhereMapFragment.MAP_BY_LATLNG) {
                fragment = WhereMapFragment.newInstance(latitude, longitude, viewMapBy);
            }
            fm.beginTransaction()
                    .add(R.id.fragment_where_map_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_where_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
