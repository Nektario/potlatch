package com.mocca_capstone.potlatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.providers.FollowedUsers;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.IconColorizer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/28/2014.
 */
public class FollowedUsersAdapter extends BaseAdapter {
    private static final String TAG = "RankingsAdapter";
    @Inject ImageLoader mImageloader;
    @Inject LayoutInflater mInflater;
    @Inject FollowedUsers mFollowedUsers;
    private Context mContext;
    private IconColorizer mIconColorizer;
    private List<User> mList;


    public FollowedUsersAdapter(Context context, List<User> users) {
        Injector.getInstance().inject(this);
        mContext = context;
        mIconColorizer = new IconColorizer(context);
        mList = new ArrayList<User>();
        mList.addAll(users);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public User getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.followed_user_list_item, parent, false);
            holder.ownerProfilePhoto = (ImageView) convertView.findViewById(R.id.following_profile_pic);
            holder.ownerName = (TextView) convertView.findViewById(R.id.following_profile_name);
            holder.followIcon = (ImageView) convertView.findViewById(R.id.following_follow_user);
            FontManager.setLight(mContext, holder.ownerName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User followedUser = getItem(position);
        mImageloader.loadWithoutDecoration(followedUser.getProfilePhotoUrl(), holder.ownerProfilePhoto);
        holder.ownerName.setText(followedUser.getProfileName());

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

        colorizeFollowIcon(holder.followIcon, mFollowedUsers.isFollowedUser(followedUser));
        return convertView;
    }

    private static class ViewHolder {
        public ImageView ownerProfilePhoto;
        public TextView ownerName;
        public ImageView followIcon;
    }

    private void colorizeFollowIcon(ImageView icon, boolean isFollowedUser) {
        if (isFollowedUser) {
            mIconColorizer.setFollowedColor(icon);
        } else {
            mIconColorizer.setNoColor(icon);
        }
    }
}
