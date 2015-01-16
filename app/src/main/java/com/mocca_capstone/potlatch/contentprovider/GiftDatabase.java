package com.mocca_capstone.potlatch.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/14/2014.
 */
public final class GiftDatabase extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "gifts.db";
    private static final int DATABASE_VERSION = 1;
    private static final String GIFTS_TABLE_NAME = "gifts";


    GiftDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOGD(TAG, "Creating Database");
        db.execSQL("CREATE TABLE " + GIFTS_TABLE_NAME + " ("
                + GiftContract.Gifts._ID + " INTEGER PRIMARY KEY,"
                + GiftContract.Gifts.PARENT_ID + " INTEGER,"
                + GiftContract.Gifts.CREATED_TIME + " TEXT,"
                + GiftContract.Gifts.MODIFIED_TIME + " TEXT,"
                + GiftContract.Gifts.IS_CHAIN_ROOT + " INTEGER,"
                + GiftContract.Gifts.TITLE + " TEXT,"
                + GiftContract.Gifts.DESCRIPTION + " TEXT,"
                + GiftContract.Gifts.LATITUDE + " REAL,"
                + GiftContract.Gifts.LONGITUDE + " REAL,"
                + GiftContract.Gifts.TOUCH_COUNT + " INTEGER,"
                + GiftContract.Gifts.FLAG_COUNT + " INTEGER,"
                + GiftContract.Gifts.TOUCHED_USERS + " TEXT,"
                + GiftContract.Gifts.FLAGGED_BY_USERS + " TEXT,"
                + GiftContract.Gifts.MEDIA_NAME + " TEXT,"
                + GiftContract.Gifts.MEDIA_MIME_TYPE + " TEXT,"
                + GiftContract.Gifts.MEDIA_URL + " TEXT,"
                + GiftContract.Gifts.OWNER_ID + " INTEGER,"
                + GiftContract.Gifts.OWNER_PROFILE_NAME + " TEXT,"
                + GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "Upgrading database from version " + oldVersion + " to "
                                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }
}
