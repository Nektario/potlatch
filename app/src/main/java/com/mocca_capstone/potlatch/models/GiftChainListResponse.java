package com.mocca_capstone.potlatch.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nektario on 10/13/2014.
 */
public class GiftChainListResponse {
    @SerializedName("thisPageElementCount")
    private long mThisPageElementCount;
    @SerializedName("thisPageNumber")
    private int mThisPageNumber;
    @SerializedName("totalElementCount")
    private long mTotalElementCount;
    @SerializedName("totalPages")
    private int mTotalPages;
    @SerializedName("nextPageToken")
    private int mNextPageToken;
    @SerializedName("isFirstPage")
    private boolean mIsFirstPage;
    @SerializedName("isLastPage")
    private boolean mIsLastPage;

    @SerializedName("gifts")
    public List<Gift> mGifts;


    public boolean hasMoreResults() {
        return !mIsLastPage;
    }
    // Getters
    public long getThisPageElementCount() {
        return mThisPageElementCount;
    }
    public long getThisPageNumber() {
        return mThisPageNumber;
    }
    public long getTotalElementCount() {
        return mTotalElementCount;
    }
    public long getTotalPages() {
        return mTotalPages;
    }
    public int getNextPageToken() {
        return mNextPageToken;
    }
    public boolean isFirstPage() {
        return mIsFirstPage;
    }
    public boolean isLastPage() {
        return mIsLastPage;
    }

    public List<Gift> getGifts() {
        return mGifts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mThisPageElementCount);
        sb.append(", ");
        sb.append(mThisPageNumber);
        sb.append(", ");
        sb.append(mTotalElementCount);
        sb.append(", ");
        sb.append(mTotalPages);
        sb.append(", ");
        sb.append(mNextPageToken);
        sb.append(", ");
        sb.append(mIsFirstPage);
        sb.append(", ");
        sb.append(mIsLastPage);

        return sb.toString();
    }
}
