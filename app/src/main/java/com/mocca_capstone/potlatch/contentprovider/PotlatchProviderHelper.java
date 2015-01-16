package com.mocca_capstone.potlatch.contentprovider;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.google.common.collect.Lists;
import com.mocca_capstone.potlatch.application.PotlatchApp;
import com.mocca_capstone.potlatch.models.BaseUser;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.models.Votable;

import java.util.ArrayList;
import java.util.List;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/15/2014.
 */
public class PotlatchProviderHelper {
    private static final String TAG = "PotlatchProviderHelper";


    public static Gift getNewestGift(ContentResolver cr) {
        Gift gift;

        Cursor c = cr.query(GiftContract.Gifts.CONTENT_URI, null, null, null, GiftContract.Gifts._ID+" DESC LIMIT 1");
        if (c.getCount() == 1) {
            c.moveToFirst();
            gift = new Gift(c);
        } else {
            gift = new Gift();
        }
        c.close();

        return gift;
    }

    public static long getTotalTouchesForUser(ContentResolver cr, BaseUser user) {
        Cursor c = cr.query(GiftContract.Gifts.CONTENT_URI,
                            new String[]{"sum("+GiftContract.Gifts.TOUCH_COUNT+") as " + GiftContract.Gifts.TOUCH_COUNT},
                            GiftContract.Gifts.OWNER_ID + "=?",
                            new String[]{String.valueOf(user.getId())},
                            null);

        c.moveToFirst();
        return c.getLong(0);
    }

    public static long getTotalFlagsForUser(ContentResolver cr, BaseUser user) {
        Cursor c = cr.query(GiftContract.Gifts.CONTENT_URI,
                new String[]{"sum("+GiftContract.Gifts.FLAG_COUNT+") as " + GiftContract.Gifts.FLAG_COUNT},
                GiftContract.Gifts.OWNER_ID + "=?",
                new String[]{String.valueOf(user.getId())},
                null);

        c.moveToFirst();
        return c.getLong(0);
    }

    public static void syncNow() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(PotlatchApp.getSyncAccount(), GiftContract.AUTHORITY, settingsBundle);
    }

    public static void enablePeriodicSync(Account account, long pollFrequencySeconds) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);

        disablePeriodicSync(account);
        ContentResolver.setIsSyncable(account, GiftContract.AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, GiftContract.AUTHORITY, settingsBundle, pollFrequencySeconds);
        ContentResolver.setSyncAutomatically(account, GiftContract.AUTHORITY, true);
    }

    public static void disablePeriodicSync(Account account) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        ContentResolver.removePeriodicSync(account, GiftContract.AUTHORITY, settingsBundle);
    }

    public static void setTouch(ContentResolver cr, Votable votable) {
        ContentValues values = new ContentValues();
        values.put(GiftContract.Gifts.TOUCHED_USERS, votable.getTouchedUsersAsString());
        values.put(GiftContract.Gifts.TOUCH_COUNT, votable.getTouchCount());
        cr.update(GiftContract.Gifts.CONTENT_URI, values,
                  GiftContract.Gifts._ID +"=?", new String[]{String.valueOf(votable.getId())});
    }

    public static void setFlag(ContentResolver cr, Votable votable) {
        ContentValues values = new ContentValues();
        values.put(GiftContract.Gifts.FLAGGED_BY_USERS, votable.getFlaggedByUsersAsString());
        values.put(GiftContract.Gifts.FLAG_COUNT, votable.getFlagCount());
        cr.update(GiftContract.Gifts.CONTENT_URI, values,
                GiftContract.Gifts._ID +"=?", new String[]{String.valueOf(votable.getId())});
    }

    public static void insert(ContentResolver cr, List<Gift> gifts) {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        for (Gift gift : gifts) {
            batch.add(ContentProviderOperation.newInsert(GiftContract.Gifts.CONTENT_URI)
                    .withValue(GiftContract.Gifts._ID, gift.getId())
                    .withValue(GiftContract.Gifts.PARENT_ID, gift.getParentId())
                    .withValue(GiftContract.Gifts.CREATED_TIME, gift.getCreatedTime())
                    .withValue(GiftContract.Gifts.MODIFIED_TIME, gift.getModifiedTime())
                    .withValue(GiftContract.Gifts.IS_CHAIN_ROOT, gift.isChainRoot())
                    .withValue(GiftContract.Gifts.TITLE, gift.getTitle())
                    .withValue(GiftContract.Gifts.DESCRIPTION, gift.getDescription())
                    .withValue(GiftContract.Gifts.LATITUDE, gift.getLatitude())
                    .withValue(GiftContract.Gifts.LONGITUDE, gift.getLongitude())
                    .withValue(GiftContract.Gifts.TOUCH_COUNT, gift.getTouchCount())
                    .withValue(GiftContract.Gifts.FLAG_COUNT, gift.getFlagCount())
                    .withValue(GiftContract.Gifts.TOUCHED_USERS, gift.getTouchedUsersAsString())
                    .withValue(GiftContract.Gifts.FLAGGED_BY_USERS, gift.getFlaggedByUsersAsString())
                    .withValue(GiftContract.Gifts.MEDIA_NAME, gift.getMediaName())
                    .withValue(GiftContract.Gifts.MEDIA_MIME_TYPE, gift.getMediaMimeType())
                    .withValue(GiftContract.Gifts.MEDIA_URL, gift.getMediaUrl())
                    .withValue(GiftContract.Gifts.OWNER_ID, gift.getOwnerId())
                    .withValue(GiftContract.Gifts.OWNER_PROFILE_NAME, gift.getOwnerProfileName())
                    .withValue(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL, gift.getOwnerProfilePhotoUrl())
                    .build());
        }
        try {
            cr.applyBatch(GiftContract.AUTHORITY, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
