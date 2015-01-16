/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mocca_capstone.potlatch.effects;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mocca_capstone.potlatch.views.AddGiftActivity;

public class BaseEffects {
    protected RenderScript mRS;
    protected Allocation mInPixelsAllocation;
    protected Allocation mOutPixelsAllocation;
    protected AddGiftActivity mActivity;

    private class MessageProcessor extends RenderScript.RSMessageHandler {
        AddGiftActivity mAct;

        MessageProcessor(AddGiftActivity act) {
            mAct = act;
        }

        public void run() {
            mAct.updateDisplay();
        }
    }

    // Override to update ui elements
    public void onSaturationBarChanged(int progress) {
    }
    public void onContrastBarChanged(int progress) {
    }
    public void onBrightnessBarChanged(int progress) {
    }

    public final void createBaseEffect(AddGiftActivity addGiftActivity) {
        mActivity = addGiftActivity;
        mRS = addGiftActivity.mRS;
        mRS.setMessageHandler(new MessageProcessor(mActivity));

        mInPixelsAllocation = addGiftActivity.mInPixelsAllocation;
        mOutPixelsAllocation = addGiftActivity.mOutPixelsAllocation;

        createEffect(mActivity.getResources());
    }

    // Must override
    public void createEffect(android.content.res.Resources res) {
    }

    // Must override
    public void runEffect() {
    }

    final public void runEffectSendMessage() {
        runEffect();
        mRS.sendMessage(0, null);
    }

    public void finish() {
        mRS.finish();
    }

    public void destroy() {
        mRS.setMessageHandler(null);
    }

    public void updateBitmap(Bitmap b) {
        mOutPixelsAllocation.copyTo(b);
    }
}
