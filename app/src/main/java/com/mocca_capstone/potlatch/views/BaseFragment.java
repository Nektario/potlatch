package com.mocca_capstone.potlatch.views;

import android.app.Fragment;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.utilities.DeviceUtils;

/**
 * Created by nektario on 10/13/2014.
 */
public abstract class BaseFragment extends Fragment implements TopPaddableFragment {

    @Override
    public void onResume() {
        super.onResume();

        int actionBarSize = DeviceUtils.calculateActionBarSizeInPixels(getActivity());
        int additionalPadding = getResources().getDimensionPixelSize(R.dimen.base_padding_level);
        setContentTopPadding(actionBarSize + additionalPadding);
    }

    @Override
    public void setContentTopPadding(int topPadding) {
        // do nothing at the base level.  This can be overrident for fragments that need to.
    }
}
