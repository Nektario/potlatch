package com.mocca_capstone.potlatch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.models.Ranking;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.providers.FollowedUsers;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.IconColorizer;
import com.mocca_capstone.potlatch.views.ProfileActivity;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/25/2014.
 */
public class RankingsAdapter extends CursorAdapter {
    private static final String TAG = "RankingsAdapter";
    @Inject ImageLoader mImageloader;
    @Inject LayoutInflater mInflater;
    @Inject FollowedUsers mFollowedUsers;
    @Inject UserAccounts mUserAccounts;
    private Context mContext;
    private IconColorizer mIconColorizer;

    private static class ViewHolder {
        public ImageView ownerProfilePhoto;
        public TextView ownerName;
        public LinearLayout touchGroup;
        public LinearLayout flagGroup;
        public TextView touchCount;
        public TextView flagCount;
        public ImageView touchIcon;
        public ImageView flagIcon;
        public ImageView followIcon;
    }


    public RankingsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        Injector.getInstance().inject(this);
        mContext = context;
        mIconColorizer = new IconColorizer(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.ranking_list_item, parent, false);
        holder.ownerProfilePhoto = (ImageView) view.findViewById(R.id.ranking_profile_pic);
        holder.ownerName = (TextView) view.findViewById(R.id.ranking_profile_name);
        holder.followIcon = (ImageView) view.findViewById(R.id.ranking_follow_user);
        holder.touchGroup = (LinearLayout) view.findViewById(R.id.gift_touch_group);
        holder.flagGroup = (LinearLayout) view.findViewById(R.id.gift_flag_group);
        holder.touchCount = (TextView) view.findViewById(R.id.gift_touch_count);
        holder.flagCount = (TextView) view.findViewById(R.id.gift_flag_count);
        holder.touchIcon = (ImageView) view.findViewById(R.id.gift_touch_icon);
        holder.flagIcon = (ImageView) view.findViewById(R.id.gift_flag_icon);
        FontManager.setLight(context, holder.ownerName);
        FontManager.setLight(context, holder.touchCount);
        FontManager.setLight(context, holder.flagCount);

        view.setTag(holder);
        return view;
    }



    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final Ranking ranking = new Ranking(cursor);
        final User followedUser = new User(ranking.getOwnerId(),
                                           ranking.getOwnerProfileName(),
                                           ranking.getOwnerProfilePhotoUrl());
        mImageloader.loadWithoutDecoration(ranking.getOwnerProfilePhotoUrl(), holder.ownerProfilePhoto);
        holder.ownerName.setText(ranking.getOwnerProfileName());
        holder.touchCount.setText(String.valueOf(ranking.getTouchCount()));
        holder.flagCount.setText(String.valueOf(ranking.getFlagCount()));

        holder.followIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFollowedUsers.isFollowedUser(followedUser)) {
                    LOGD(TAG, "UNFOLLOW: " + followedUser.toString());
                    mFollowedUsers.unfollowUser(followedUser);
                } else {
                    LOGD(TAG, "FOLLOW: " + followedUser.toString());
                    mFollowedUsers.followUser(followedUser);
                }
                notifyDataSetChanged();
            }
        });

        // hide follow icon when displaying self user
        if (mUserAccounts.isLoggedIn() && mUserAccounts.getActiveUser().getId() == followedUser.getId()) {
            holder.followIcon.setVisibility(View.GONE);
        }

        colorizeFollowIcon(holder.followIcon, mFollowedUsers.isFollowedUser(followedUser));
        colorizeTouchIcons(holder.touchIcon);
        colorizeFlagIcons(holder.flagIcon);
    }

    private void colorizeTouchIcons(ImageView icon) {
        mIconColorizer.setTouchedColor(icon);
    }

    private void colorizeFlagIcons(ImageView icon) {
        mIconColorizer.setFlaggedColor(icon);
    }

    private void colorizeFollowIcon(ImageView icon, boolean isFollowedUser) {
        if (isFollowedUser) {
            mIconColorizer.setFollowedColor(icon);
        } else {
            mIconColorizer.setNoColor(icon);
        }
    }
}
