package com.android.arthlimchiu.where;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.github.clans.fab.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class WherePlaceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "WherePlaceFragment";
    private static final int WHERE_PLACE_LOADER = 2;

    private ListView mListView;
    private WherePlaceListAdapter mAdapter;

    private FloatingActionButton fab;

    public WherePlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_where_place, container, false);

        mListView = (ListView) v.findViewById(R.id.fragment_where_place_listView);

        getLoaderManager().initLoader(WHERE_PLACE_LOADER, null, this);

        fab = (FloatingActionButton) v.findViewById(R.id.fragment_where_place_fab);
        fab.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show(true);
                fab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_scale_down));
                fab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_scale_up));
            }
        }, 300);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WhereNewPlaceActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_where_place, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Places");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{PlaceTable.COLUMN_ID, PlaceTable.COLUMN_PLACE_NAME, PlaceTable.COLUMN_ADDRESS};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), WhereContentProvider.CONTENT_URI_PLACES, projection, null, null, "date desc");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new WherePlaceListAdapter(getActivity(), data);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListView.setAdapter(null);
    }

    class WherePlaceListAdapter extends CursorAdapter {

        public WherePlaceListAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View v = LayoutInflater.from(context).inflate(R.layout.fragment_where_place_list_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.mPlaceNameTv = (TextView) v.findViewById(R.id.fragment_where_place_list_item_placeName);
            holder.mAddressTv = (TextView) v.findViewById(R.id.fragment_where_place_list_item_address);

            v.setTag(holder);

            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            String placeName = cursor.getString(cursor.getColumnIndex(PlaceTable.COLUMN_PLACE_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PlaceTable.COLUMN_ADDRESS));
            holder.mPlaceNameTv.setText(placeName);
            holder.mAddressTv.setText(address);
        }

        class ViewHolder {
            TextView mPlaceNameTv;
            TextView mAddressTv;
        }
    }
}
