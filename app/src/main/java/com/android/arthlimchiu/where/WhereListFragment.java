package com.android.arthlimchiu.where;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.arthlimchiu.where.contentprovider.WhereContentProvider;
import com.android.arthlimchiu.where.database.PlaceTable;
import com.android.arthlimchiu.where.database.WhereTable;
import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhereListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "WhereListFragment";

    private static final int WHERE_LIST_LOADER = 0;

    public static final String KEY_TRACK_ID = "KEY_TRACK_ID";

    ListView mListView;
    WhereListAdapter mAdapter;

    long trackId;

    public WhereListFragment() {
        // Required empty public constructor
    }

    public static WhereListFragment newInstance(long trackId) {
        Bundle args = new Bundle();
        args.putLong(KEY_TRACK_ID, trackId);

        WhereListFragment fragment = new WhereListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();

        if (args != null) {
            trackId = args.getLong(KEY_TRACK_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Wheres");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_where_list, container, false);

        mListView = (ListView) v.findViewById(R.id.fragment_where_list_listView);

        getLoaderManager().initLoader(WHERE_LIST_LOADER, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{"wheres."+WhereTable.COLUMN_ID, WhereTable.COLUMN_TIME_IN, WhereTable.COLUMN_TIME_OUT,
                WhereTable.COLUMN_TRACK_ID, WhereTable.COLUMN_PLACE_ID, WhereTable.COLUMN_STATUS, PlaceTable.COLUMN_PLACE_NAME};
        String[] selectionArgs = new String[]{String.valueOf(trackId)};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), WhereContentProvider.CONTENT_URI_WHERES, projection, null, selectionArgs, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new WhereListAdapter(getActivity(), data);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListView.setAdapter(null);
    }

    class WhereListAdapter extends CursorAdapter {

        public WhereListAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = LayoutInflater.from(context).inflate(R.layout.fragment_where_list_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.mPlaceNameTv = (TextView) v.findViewById(R.id.fragment_where_list_item_placeName);
            holder.mTimeStampTv = (TextView) v.findViewById(R.id.fragment_where_list_item_timestamp);
            holder.mDurationTv = (TextView) v.findViewById(R.id.fragment_where_list_item_duration);

            v.setTag(holder);

            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            long timeIn = cursor.getLong(cursor.getColumnIndex(WhereTable.COLUMN_TIME_IN));
            long timeOut = cursor.getLong(cursor.getColumnIndex(WhereTable.COLUMN_TIME_OUT));
            int placeId = cursor.getInt(cursor.getColumnIndex(WhereTable.COLUMN_PLACE_ID));
            int trackId = cursor.getInt(cursor.getColumnIndex(WhereTable.COLUMN_TRACK_ID));
            int status = cursor.getInt(cursor.getColumnIndex(WhereTable.COLUMN_STATUS));
            String placeName = cursor.getString(cursor.getColumnIndex(PlaceTable.COLUMN_PLACE_NAME));

            String timeStamp;
            long hrs;
            long mins;
            long sec;
            long totalTime;

            Log.i(TAG, "Time out: " + timeOut);
            if (status == 1) {
                totalTime = System.currentTimeMillis() - timeIn;
                timeStamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timeIn) + "-Present";
                hrs = TimeUnit.MILLISECONDS.toHours(totalTime);
                mins = TimeUnit.MILLISECONDS.toMinutes(totalTime);
                sec = totalTime / 1000 % 60;
            } else {
                totalTime = timeOut - timeIn;
                timeStamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timeIn) + "-" +
                        new SimpleDateFormat("h:mm a", Locale.getDefault()).format(timeOut);

                hrs = TimeUnit.MILLISECONDS.toHours(totalTime);
                mins = TimeUnit.MILLISECONDS.toMinutes(totalTime);
                sec = totalTime / 1000 % 60;
            }

            //holder.mPlaceNameTv.setText("Track ID: " + trackId + " Place ID: " + placeId + " " + placeName);
            holder.mPlaceNameTv.setText(placeName);
            holder.mTimeStampTv.setText(timeStamp);


//
//            long hrs = totalTime / (60 * 60 * 1000);
//            long mins = totalTime / (60 * 1000) % 60;
//            long sec = totalTime / 1000 % 60;


            holder.mDurationTv.setText(hrs + " hr " + mins + " min " + sec + " s");
        }

        class ViewHolder {
            TextView mPlaceNameTv;
            TextView mTimeStampTv;
            TextView mDurationTv;
        }
    }
}