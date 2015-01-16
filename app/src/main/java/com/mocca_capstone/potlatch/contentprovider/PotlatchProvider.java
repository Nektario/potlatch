package com.mocca_capstone.potlatch.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.mocca_capstone.potlatch.models.Gift;

import java.util.HashMap;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/14/2014.
 */
public class PotlatchProvider extends ContentProvider {
    private static final String TAG = "PotlatchProvider";
    private static final int GIFTS = 1;
    private static final int GIFTS_ID = 2;
    private static final int RANKINGS = 3;

    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sGiftsProjectionMap;
    private static HashMap<String, String> sRankingsProjectionMap;
    private GiftDatabase mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(GiftContract.AUTHORITY, GiftContract.Gifts.TABLE_NAME, GIFTS);
        sUriMatcher.addURI(GiftContract.AUTHORITY, GiftContract.Gifts.TABLE_NAME + "/#", GIFTS_ID);
        sUriMatcher.addURI(GiftContract.AUTHORITY, GiftContract.Gifts.TABLE_NAME + "/Rankings", RANKINGS);

        sGiftsProjectionMap = new HashMap<String, String>();
        sGiftsProjectionMap.put(GiftContract.Gifts._ID, GiftContract.Gifts._ID);
        sGiftsProjectionMap.put(GiftContract.Gifts.PARENT_ID, GiftContract.Gifts.PARENT_ID);
        sGiftsProjectionMap.put(GiftContract.Gifts.CREATED_TIME, GiftContract.Gifts.CREATED_TIME);
        sGiftsProjectionMap.put(GiftContract.Gifts.MODIFIED_TIME, GiftContract.Gifts.MODIFIED_TIME);
        sGiftsProjectionMap.put(GiftContract.Gifts.IS_CHAIN_ROOT, GiftContract.Gifts.IS_CHAIN_ROOT);
        sGiftsProjectionMap.put(GiftContract.Gifts.TITLE, GiftContract.Gifts.TITLE);
        sGiftsProjectionMap.put(GiftContract.Gifts.DESCRIPTION, GiftContract.Gifts.DESCRIPTION);
        sGiftsProjectionMap.put(GiftContract.Gifts.LATITUDE, GiftContract.Gifts.LATITUDE);
        sGiftsProjectionMap.put(GiftContract.Gifts.LONGITUDE, GiftContract.Gifts.LONGITUDE);
        sGiftsProjectionMap.put(GiftContract.Gifts.TOUCH_COUNT, GiftContract.Gifts.TOUCH_COUNT);
        sGiftsProjectionMap.put(GiftContract.Gifts.FLAG_COUNT, GiftContract.Gifts.FLAG_COUNT);
        sGiftsProjectionMap.put(GiftContract.Gifts.TOUCHED_USERS, GiftContract.Gifts.TOUCHED_USERS);
        sGiftsProjectionMap.put(GiftContract.Gifts.FLAGGED_BY_USERS, GiftContract.Gifts.FLAGGED_BY_USERS);
        sGiftsProjectionMap.put(GiftContract.Gifts.MEDIA_NAME, GiftContract.Gifts.MEDIA_NAME);
        sGiftsProjectionMap.put(GiftContract.Gifts.MEDIA_MIME_TYPE, GiftContract.Gifts.MEDIA_MIME_TYPE);
        sGiftsProjectionMap.put(GiftContract.Gifts.MEDIA_URL, GiftContract.Gifts.MEDIA_URL);
        sGiftsProjectionMap.put(GiftContract.Gifts.OWNER_ID, GiftContract.Gifts.OWNER_ID);
        sGiftsProjectionMap.put(GiftContract.Gifts.OWNER_PROFILE_NAME, GiftContract.Gifts.OWNER_PROFILE_NAME);
        sGiftsProjectionMap.put(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL, GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL);

        sRankingsProjectionMap = new HashMap<String, String>();
        sRankingsProjectionMap.put(GiftContract.Gifts.OWNER_ID, GiftContract.Gifts.OWNER_ID);
        sRankingsProjectionMap.put(GiftContract.Gifts.OWNER_PROFILE_NAME, GiftContract.Gifts.OWNER_PROFILE_NAME);
        sRankingsProjectionMap.put(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL, GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL);
        sRankingsProjectionMap.put(GiftContract.Gifts.TOUCH_COUNT, GiftContract.Gifts.TOUCH_COUNT);
        sRankingsProjectionMap.put(GiftContract.Gifts.FLAG_COUNT, GiftContract.Gifts.FLAG_COUNT);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new GiftDatabase(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String groupBy = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(GiftContract.Gifts.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case GIFTS :
                qb.setProjectionMap(sGiftsProjectionMap);
                break;

            case GIFTS_ID :
                qb.setProjectionMap(sGiftsProjectionMap);
                qb.appendWhere(GiftContract.Gifts._ID + "=" + uri.getPathSegments().get(1));
                break;

            case RANKINGS :
                qb.setProjectionMap(sRankingsProjectionMap);
                sortOrder = GiftContract.Gifts.TOUCH_COUNT + " DESC";
                groupBy = GiftContract.Gifts.OWNER_ID;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = GiftContract.Gifts.DEFAULT_SORT;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String sql = qb.buildQuery(projection, selection, groupBy, null, orderBy, null);
        Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy, null, orderBy);
        LOGD(TAG, sql + " - rows: " + c.getCount());

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case GIFTS :
                return GiftContract.Gifts.CONTENT_TYPE;

            case GIFTS_ID:
                return GiftContract.Gifts.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != GIFTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }


        // data integrity
        Long now = System.currentTimeMillis();
        verifyTitle(values);
        verifyMediaName(values);
        verifyMediaMimeType(values);
        verifyMediaUrl(values);
        verifyOwnerId(values);
        verifyOwnerProfileName(values);
        verifyOwnerProfilePhotoUrl(values);
        makeDefaultParentId(values);
        makeDefaultCreatedTime(values, now);
        makeDefaultModifiedTime(values, now);
        makeDefaultIsChainRoot(values);
        makeDefaultDescription(values);
        makeDefaultLatitude(values);
        makeDefaultLongitude(values);
        makeDefaultTouchCount(values);
        makeDefaultFlagCount(values);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.replace(GiftContract.Gifts.TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri giftUri = ContentUris.withAppendedId(GiftContract.Gifts.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(giftUri, null);
            return giftUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case GIFTS:
                count = db.delete(GiftContract.Gifts.TABLE_NAME, where, whereArgs);
                break;

            case GIFTS_ID:
                String giftId = uri.getPathSegments().get(1);
                count = db.delete(GiftContract.Gifts.TABLE_NAME, GiftContract.Gifts._ID + "=" + giftId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case GIFTS:
                count = db.update(GiftContract.Gifts.TABLE_NAME, values, where, whereArgs);
                break;

            case GIFTS_ID:
                String giftId = uri.getPathSegments().get(1);
                count = db.update(GiftContract.Gifts.TABLE_NAME, values, GiftContract.Gifts._ID + "=" + giftId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private void verifyTitle(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.TITLE)) {
            handleMissingData(GiftContract.Gifts.TITLE);
        }
    }
    private void verifyMediaName(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.MEDIA_NAME)) {
            handleMissingData(GiftContract.Gifts.MEDIA_NAME);
        }
    }
    private void verifyMediaMimeType(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.MEDIA_MIME_TYPE)) {
            handleMissingData(GiftContract.Gifts.MEDIA_MIME_TYPE);
        }
    }
    private void verifyMediaUrl(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.MEDIA_URL)) {
            handleMissingData(GiftContract.Gifts.MEDIA_URL);
        }
    }
    private void verifyOwnerId(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.OWNER_ID)) {
            handleMissingData(GiftContract.Gifts.OWNER_ID);
        }
    }
    private void verifyOwnerProfileName(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.OWNER_PROFILE_NAME)) {
            handleMissingData(GiftContract.Gifts.OWNER_PROFILE_NAME);
        }
    }
    private void verifyOwnerProfilePhotoUrl(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL)) {
            handleMissingData(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL);
        }
    }

    private void handleMissingData(String fieldName) {
        throw new SQLException(fieldName + " is required");
    }

    private void makeDefaultParentId(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.PARENT_ID)) {
            values.put(GiftContract.Gifts.PARENT_ID, Gift.CHAIN_ROOT_PARENT_ID);
        }
    }
    private void makeDefaultCreatedTime(ContentValues values, long now) {
        if (!values.containsKey(GiftContract.Gifts.CREATED_TIME)) {
            values.put(GiftContract.Gifts.CREATED_TIME, now);
        }
    }
    private void makeDefaultModifiedTime(ContentValues values, long now) {
        if (!values.containsKey(GiftContract.Gifts.MODIFIED_TIME)) {
            values.put(GiftContract.Gifts.MODIFIED_TIME, now);
        }
    }
    private void makeDefaultIsChainRoot(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.IS_CHAIN_ROOT)) {
            values.put(GiftContract.Gifts.IS_CHAIN_ROOT, true);
        }
    }
    private void makeDefaultDescription(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.DESCRIPTION)) {
            values.put(GiftContract.Gifts.DESCRIPTION, "");
        }
    }
    private void makeDefaultLatitude(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.LATITUDE)) {
            values.put(GiftContract.Gifts.LATITUDE, 0.0);
        }
    }
    private void makeDefaultLongitude(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.LONGITUDE)) {
            values.put(GiftContract.Gifts.LONGITUDE, 0.0);
        }
    }
    private void makeDefaultTouchCount(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.TOUCH_COUNT)) {
            values.put(GiftContract.Gifts.TOUCH_COUNT, 0);
        }
    }
    private void makeDefaultFlagCount(ContentValues values) {
        if (!values.containsKey(GiftContract.Gifts.FLAG_COUNT)) {
            values.put(GiftContract.Gifts.FLAG_COUNT, 0);
        }
    }
}
