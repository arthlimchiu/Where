package com.android.arthlimchiu.where;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.TrackTable;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhereTrackListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int WHERE_TRACK_LIST_LOADER = 1;

    ListView mListView;

    TrackListAdapter mAdapter;

    public WhereTrackListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_where_track_list, container, false);

        mListView = (ListView) v.findViewById(R.id.fragment_where_track_list_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WhereActivity.class);
                intent.putExtra(WhereListFragment.KEY_TRACK_ID, id);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(WHERE_TRACK_LIST_LOADER, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{TrackTable.COLUMN_ID, TrackTable.COLUMN_DATE};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), WhereContentProvider.CONTENT_URI_TRACKS, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new TrackListAdapter(getActivity(), data);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListView.setAdapter(null);
    }

    class TrackListAdapter extends CursorAdapter {

        public TrackListAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View v = LayoutInflater.from(context).inflate(R.layout.fragment_where_track_list_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.mTrackTv = (TextView) v.findViewById(R.id.fragment_where_track_list_item_name);

            v.setTag(holder);

            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            long date = cursor.getLong(cursor.getColumnIndex(TrackTable.COLUMN_DATE));
            holder.mTrackTv.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(date));

        }

        class ViewHolder {
            TextView mTrackTv;
        }
    }
}
