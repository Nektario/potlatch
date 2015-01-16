package com.mocca_capstone.potlatch.network;

import com.mocca_capstone.potlatch.models.Votable;
import com.mocca_capstone.potlatch.models.Vote;

import java.io.Serializable;

/**
 * Created by nektario on 10/21/2014.
 */
public class VoteCommand implements ApiCommand, Serializable {
    private Votable mVotable;
    private Vote mVote;

    public VoteCommand(Votable votable, Vote vote) {
        mVotable = votable;
        mVote = vote;
    }

    @Override
    public void execute(GiftApiClient apiClient) {
        apiClient.vote(mVotable, mVote);
    }
}
