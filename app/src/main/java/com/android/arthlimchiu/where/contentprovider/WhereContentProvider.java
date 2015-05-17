package com.android.arthlimchiu.where.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.android.arthlimchiu.where.database.PlaceTable;
import com.android.arthlimchiu.where.database.TrackTable;
import com.android.arthlimchiu.where.database.WhereDatabaseHelper;
import com.android.arthlimchiu.where.database.WhereTable;

/**
 * Created by Clarence on 4/24/2015.
 */
public class WhereContentProvider extends ContentProvider   {

    private WhereDatabaseHelper database;

    // Used for the UriMatcher
    private static final int TRACKS = 1;
    private static final int TRACK_ID = 2;
    private static final int PLACES = 3;
    private static final int PLACE_ID = 4;
    private static final int WHERES = 5;
    private static final int WHERE_ID = 6;

    private static final String AUTHORITY = "com.android.arthlimchiu.where.contentprovider";
    private static final String PATH_TRACKS = "tracks";
    private static final String PATH_PLACES = "places";
    private static final String PATH_WHERES = "wheres";

    //private static final String BASE_PATH = "wheres";
    public static final Uri CONTENT_URI_TRACKS = Uri.parse("content://" + AUTHORITY + "/" + PATH_TRACKS);
    public static final Uri CONTENT_URI_PLACES = Uri.parse("content://" + AUTHORITY + "/" + PATH_PLACES);
    public static final Uri CONTENT_URI_WHERES = Uri.parse("content://" + AUTHORITY + "/" + PATH_WHERES);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/wheres";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/where";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, PATH_TRACKS, TRACKS);
        sURIMatcher.addURI(AUTHORITY, PATH_TRACKS + "/#", TRACK_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_PLACES, PLACES);
        sURIMatcher.addURI(AUTHORITY, PATH_PLACES + "/#", PLACE_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_WHERES, WHERES);
        sURIMatcher.addURI(AUTHORITY, PATH_WHERES + "/#", WHERE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new WhereDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();


        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TRACKS:
                builder.setTables(TrackTable.TABLE_TRACK);
                break;
            case TRACK_ID:
                // adding the ID to the original query
                builder.setTables(TrackTable.TABLE_TRACK);
                builder.appendWhere(TrackTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case PLACES:
                builder.setTables(PlaceTable.TABLE_PLACE);
                break;
            case PLACE_ID:
                // adding the ID to the original query
                builder.setTables(PlaceTable.TABLE_PLACE);
                builder.appendWhere(PlaceTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case WHERES:
                builder.setTables(WhereTable.TABLE_WHERE + ", " + PlaceTable.TABLE_PLACE);
                break;
            case WHERE_ID:
                // adding the ID to the original query
                builder.setTables(WhereTable.TABLE_WHERE);
                builder.appendWhere(WhereTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();

        long id;
        String path;

        switch (uriType) {
            case TRACKS:
                id = db.insert(TrackTable.TABLE_TRACK, null, values);
                path = PATH_TRACKS;
                break;
            case PLACES:
                id = db.insert(PlaceTable.TABLE_PLACE, null, values);
                path = PATH_PLACES;
                break;
            case WHERES:
                id = db.insert(WhereTable.TABLE_WHERE, null, values);
                path = PATH_WHERES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;

        String id;

        switch (uriType) {
            case TRACKS:
                rowsDeleted = db.delete(TrackTable.TABLE_TRACK, selection, selectionArgs);
                break;
            case TRACK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TrackTable.TABLE_TRACK, TrackTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(TrackTable.TABLE_TRACK, TrackTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case PLACES:
                rowsDeleted = db.delete(PlaceTable.TABLE_PLACE, selection, selectionArgs);
                break;
            case PLACE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(PlaceTable.TABLE_PLACE, PlaceTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(PlaceTable.TABLE_PLACE, PlaceTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case WHERES:
                rowsDeleted = db.delete(WhereTable.TABLE_WHERE, selection, selectionArgs);
                break;
            case WHERE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(WhereTable.TABLE_WHERE, WhereTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(WhereTable.TABLE_WHERE, WhereTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;

        String id;

        switch (uriType) {
            case TRACKS:
                rowsUpdated = db.update(TrackTable.TABLE_TRACK, values, selection, selectionArgs);
                break;
            case TRACK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(TrackTable.TABLE_TRACK, values, TrackTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(TrackTable.TABLE_TRACK, values, TrackTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case PLACES:
                rowsUpdated = db.update(PlaceTable.TABLE_PLACE, values, selection, selectionArgs);
                break;
            case PLACE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(PlaceTable.TABLE_PLACE, values, PlaceTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(PlaceTable.TABLE_PLACE, values, PlaceTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            case WHERES:
                rowsUpdated = db.update(WhereTable.TABLE_WHERE, values, selection, selectionArgs);
                break;
            case WHERE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(WhereTable.TABLE_WHERE, values, WhereTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(WhereTable.TABLE_WHERE, values, WhereTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
