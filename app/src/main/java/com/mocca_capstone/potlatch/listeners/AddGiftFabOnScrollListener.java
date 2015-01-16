package com.mocca_capstone.potlatch.listeners;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.mocca_capstone.potlatch.widgets.AddGiftFab;

/**
 * Created by nektario on 11/3/2014.
 */
public class AddGiftFabOnScrollListener extends FloatingActionButton.FabOnScrollListener {
    private FloatingActionButton mFloatingActionButton;
    private AddGiftFab mAddGiftFab;


    public AddGiftFabOnScrollListener(AddGiftFab addGiftFab) {
        mAddGiftFab = addGiftFab;

        setScrollDirectionListener(new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                mFloatingActionButton.show();
            }

            @Override
            public void onScrollUp() {
                mAddGiftFab.collapseInstantly();
                mFloatingActionButton.hide();
            }
        });
    }

    public void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
    }
}
