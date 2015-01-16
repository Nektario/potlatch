package com.mocca_capstone.potlatch.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.adapters.FollowedUsersAdapter;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.providers.FollowedUsers;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import javax.inject.Inject;

/**
 * Created by nektario on 10/28/2014.
 */
public class FollowedUsersFragment extends BaseFragment {
    private static final String TAG = "RankingsFragment";
    @Inject FollowedUsers mFollowedUsers;
    private FollowedUsersAdapter mAdapter;
    private EndlessCollectionView mListView;


    public static FollowedUsersFragment newInstance() {
        return new FollowedUsersFragment();
    }


    public FollowedUsersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_rankings, parent, false);
        mAdapter = new FollowedUsersAdapter(getActivity(), mFollowedUsers.getFollowedUsers());
        mListView = (EndlessCollectionView) view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startActivity(ProfileActivity.newInstance(getActivity(), (User) mListView.getItemAtPosition(position)));
            }
        });
        return view;
    }

    @Override
    public void setContentTopPadding(int topPadding) {
        mListView.setContentTopClearance(topPadding);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getActionBarToolbar().setTitle(R.string.navdrawer_item_following);
        ((BaseActivity) getActivity()).getActionBarToolbar().setSubtitle("");
    }
}
