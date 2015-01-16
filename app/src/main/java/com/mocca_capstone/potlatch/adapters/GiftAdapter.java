package com.mocca_capstone.potlatch.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.listeners.GiftCallbacks;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.Formatters;
import com.mocca_capstone.potlatch.utilities.IconColorizer;
import com.mocca_capstone.potlatch.views.DisplayPictureActivity;
import com.mocca_capstone.potlatch.views.GiftDetailsActivity;
import com.mocca_capstone.potlatch.views.ProfileActivity;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/15/2014.
 */
public class GiftAdapter extends CursorAdapter {
    private static final String TAG = "EndlessCursorAdapter";
    @Inject ImageLoader mImageloader;
    @Inject LayoutInflater mInflater;
    @Inject UserAccounts mUserAccounts;
    private Context mContext;
    private int mLastAnimatedPosition = 0;
    private GiftCallbacks mCallbacks;

    private IconColorizer mIconColorizer;

    private static class ViewHolder {
        public ImageView picture;
        public TextView title;
        public ImageView ownerProfilePhoto;
        public TextView ownerName;
        public LinearLayout touchGroup;
        public LinearLayout flagGroup;
        public TextView touchCount;
        public TextView flagCount;
        public ImageView touchIcon;
        public ImageView flagIcon;
    }


    public GiftAdapter(Context context, Cursor c, boolean autoRequery, AdapterState savedState, GiftCallbacks cb) {
        super(context, c, autoRequery);
        Injector.getInstance().inject(this);
        mContext = context;
        mCallbacks = cb;

        if (savedState != null) {
            mLastAnimatedPosition = savedState.lastAnimatedPosition;
        }

        mIconColorizer = new IconColorizer(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.gift, parent, false);
        holder.picture = (ImageView) view.findViewById(R.id.gift_picture);
        holder.title = (TextView) view.findViewById(R.id.gift_title);
        holder.ownerProfilePhoto = (ImageView) view.findViewById(R.id.gift_owner_profile_pic);
        holder.ownerName = (TextView) view.findViewById(R.id.gift_details);
        holder.touchGroup = (LinearLayout) view.findViewById(R.id.gift_touch_group);
        holder.flagGroup = (LinearLayout) view.findViewById(R.id.gift_flag_group);
        holder.touchCount = (TextView) view.findViewById(R.id.gift_touch_count);
        holder.flagCount = (TextView) view.findViewById(R.id.gift_flag_count);
        holder.touchIcon = (ImageView) view.findViewById(R.id.gift_touch_icon);
        holder.flagIcon = (ImageView) view.findViewById(R.id.gift_flag_icon);
        FontManager.setSerifLight(context, holder.title);
        FontManager.setLight(context, holder.ownerName);
        FontManager.setLight(context, holder.touchCount);
        FontManager.setLight(context, holder.flagCount);

        view.setTag(holder);
        return view;
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final Gift gift = new Gift(cursor);
        mImageloader.load(gift.getMediaUrl(), holder.picture);
        mImageloader.loadWithoutDecoration(gift.getOwnerProfilePhotoUrl(), holder.ownerProfilePhoto);
        holder.title.setText(gift.getTitle());
        holder.ownerName.setText(gift.getOwnerProfileName());
        holder.ownerName.append("\n");
        holder.ownerName.append(Formatters.formatDateTimeForLocaleIndependentDisplay(gift.getCreatedTime()));
        holder.touchCount.setText(String.valueOf(gift.getTouchCount()));
        holder.flagCount.setText(String.valueOf(gift.getFlagCount()));

        colorizeTouchIcons(gift, holder.touchIcon);
        colorizeFlagIcons(gift, holder.flagIcon);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(GiftDetailsActivity.newInstance(mContext, gift));
            }
        });
        
        holder.ownerProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User u = new User(gift.getOwnerId(),
                                        gift.getOwnerProfileName(),
                                        gift.getOwnerProfilePhotoUrl());
                mContext.startActivity(ProfileActivity.newInstance(mContext, u));
            }
        });

        holder.touchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGD(TAG, "Touched");
                if (mCallbacks != null) {
                    mCallbacks.onTouchedIconClicked(gift);
                }
            }
        });

        holder.flagGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGD(TAG, "Flagged");
                if (mCallbacks != null) {
                    mCallbacks.onFlagIconClicked(gift);
                }
            }
        });

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(DisplayPictureActivity.newInstance(mContext, gift.getMediaUrl()));
            }
        });

        animateIn(view, cursor.getPosition());
    }



    private void colorizeTouchIcons(Gift gift, ImageView icon) {
        if (giftTouchedUser(gift)) {
            mIconColorizer.setTouchedColor(icon);
        } else {
            mIconColorizer.setNoColor(icon);
        }
    }

    private void colorizeFlagIcons(Gift gift, ImageView icon) {
        if (giftWasFlaggedByUser(gift)) {
            mIconColorizer.setFlaggedColor(icon);
        } else {
            mIconColorizer.setNoColor(icon);
        }
    }

    private boolean giftTouchedUser(Gift gift) {
        return mUserAccounts.isLoggedIn() && gift.isTouchedByUser(mUserAccounts.getActiveUser().getId());
    }

    private boolean giftWasFlaggedByUser(Gift gift) {
        return mUserAccounts.isLoggedIn() && gift.isFlaggedByUser(mUserAccounts.getActiveUser().getId());
    }

    private void animateIn(View view, int position) {
        if (position > mLastAnimatedPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.list_swing_from_bottom);
            view.startAnimation(animation);
            mLastAnimatedPosition = position;
        }
    }

    public AdapterState getState() {
        AdapterState state = new AdapterState();
        state.lastAnimatedPosition = mLastAnimatedPosition;

        return state;
    }

    public void resetState() {
        mLastAnimatedPosition = 0;
    }
}
