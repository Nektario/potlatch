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
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.Formatters;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by nektario on 10/13/2014.
 */
public class EndlessListAdapter extends BaseAdapter {
    private static final String TAG = "EndlessListAdapter";
    private Context mContext;
    private List<Gift> mGifts;
    @Inject LayoutInflater mInflater;
    @Inject ImageLoader mImageloader;


    public EndlessListAdapter(Context context) {
        Injector.getInstance().inject(this);
        mGifts = new ArrayList<Gift>();
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mGifts == null) {
            return 0;
        }
        return mGifts.size();
    }

    @Override
    public Gift getItem(int position) {
        return mGifts.get(position);
    }

    public void addItem(Gift gift) {
        if (!mGifts.contains(gift)) {
            mGifts.add(gift);
        }
    }

    public void addList(List<Gift> list) {
        for (Gift gift: list) {
            addItem(gift);
        }
    }

    public void clear() {
        mGifts.clear();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mGifts.get(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gift, parent, false);
            holder.picture = (ImageView) convertView.findViewById(R.id.gift_picture);
            holder.title = (TextView) convertView.findViewById(R.id.gift_title);
            holder.ownerProfilePhoto = (ImageView) convertView.findViewById(R.id.gift_owner_profile_pic);
            holder.ownerName = (TextView) convertView.findViewById(R.id.gift_details);
            //holder.createdTime = (TextView) convertView.findViewById(R.id.gift_created_time);
            FontManager.setSerifLight(mContext, holder.title);
            FontManager.setLight(mContext, holder.ownerName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        mImageloader.load(getItem(position).getMediaUrl(), holder.picture);
        mImageloader.loadWithoutDecoration(getItem(position).getOwnerProfilePhotoUrl(), holder.ownerProfilePhoto);
        holder.title.setText(getItem(position).getTitle());
        holder.ownerName.setText(getItem(position).getOwnerProfileName());
        holder.ownerName.append("\n");
        holder.ownerName.append(Formatters.formatDateTimeForLocaleIndependentDisplay(getItem(position).getCreatedTime()));
        //holder.createdTime.setText(Formatters.formatDateTimeForLocaleIndependentDisplay(getItem(position).getCreatedTime()));
        return convertView;
    }

    private static class ViewHolder {
        public ImageView picture;
        public TextView title;
        public TextView description;
        public TextView createdTime;

        public ImageView ownerProfilePhoto;
        public TextView ownerName;
    }
}
