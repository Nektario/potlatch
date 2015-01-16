package com.mocca_capstone.potlatch.events;

import com.mocca_capstone.potlatch.models.Votable;
import com.mocca_capstone.potlatch.models.Vote;

/**
 * Created by nektario on 10/21/2014.
 */
public class EventVoteComplete {
    private boolean mIsSuccessStatusCode;
    private Votable mVotable;
    private Vote mVote;
    private String mMessage;


    public EventVoteComplete(Votable votable, Vote vote, boolean isSuccessStatusCode, String message) {
        mVotable = votable;
        mVote = vote;
        mIsSuccessStatusCode = isSuccessStatusCode;
        mMessage = message;
    }

    public boolean isSuccessStatusCode() {
        return mIsSuccessStatusCode;
    }

    public Votable getVotable() {
        return mVotable;
    }

    public Vote getVote() {
        return mVote;
    }

    public Votable.Type getVoteType() {
        return mVote.getVoteType();
    }

    public Votable.State getVoteState() {
        return mVote.getVoteState();
    }

    public String getMessage() {
        return mMessage;
    }

}
