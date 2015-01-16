package com.mocca_capstone.potlatch.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nektario on 10/14/2014.
 */
public final class GiftContract {
    public static final String AUTHORITY = "com.mocca_capstone.potlatch.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    public static interface Gifts extends BaseColumns {
        public enum Sort {
            NEW("createdTime"),
            POPULAR("touchCount"),
            MODIFIED_TIME("modifiedTime");

            private String value;

            private Sort(String val) {
                value = val;
            }

            public String getValue() {
                return value;
            }

        }
        public enum SortDirection { ASC, DESC }
        public static final String TABLE_NAME = "gifts";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mocca_capstone.gift";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mocca_capstone.gift";

        // Fields
        public static final String PARENT_ID = "parentId";
        public static final String CREATED_TIME = "createdTime";
        public static final String MODIFIED_TIME = "modifiedTime";
        public static final String IS_CHAIN_ROOT = "isChainRoot";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String TOUCH_COUNT = "touchCount";
        public static final String FLAG_COUNT = "flagCount";
        public static final String TOUCHED_USERS = "touchedUsers";
        public static final String FLAGGED_BY_USERS = "flaggedByUsers";
        public static final String MEDIA_NAME = "mediaName";
        public static final String MEDIA_MIME_TYPE = "mediaMimeType";
        public static final String MEDIA_URL = "mediaUrl";
        public static final String OWNER_ID = "ownerId";
        public static final String OWNER_PROFILE_NAME = "ownerProfileName";
        public static final String OWNER_PROFILE_PHOTO_URL = "ownerProfilePhotoUrl";

        public static final String DEFAULT_SORT = Sort.NEW.getValue() + " DESC";
    }
}
