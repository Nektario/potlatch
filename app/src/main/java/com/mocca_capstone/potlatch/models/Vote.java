package com.mocca_capstone.potlatch.models;

import java.io.Serializable;

/**
 * Created by nektario on 10/21/2014.
 */
public class Vote implements Serializable {
    private long mId;
    private Votable.State mState;
    private Votable.Type mVoteType;


    public Vote (long id, Votable.State state, Votable.Type voteType) {
        mId = id;
        mState = state;
        mVoteType = voteType;
    }

    public long getid() {
        return mId;
    }

    public Votable.Type getVoteType() {
        return mVoteType;
    }

    public Votable.State getVoteState() {
        return mState;
    }
}
