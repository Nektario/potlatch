package com.mocca_capstone.potlatch.effects;

import android.renderscript.Matrix3f;

import com.mocca_capstone.potlatch.ScriptC_levels_full;
import com.mocca_capstone.potlatch.ScriptC_levels_relaxed;


/**
 * Created by nektario on 11/16/2014.
 */
public class Levels extends BaseEffects {
    private ScriptC_levels_relaxed mScriptR;
    private ScriptC_levels_full mScriptF;
    private float mInBlack = 64.0f;
    private float mOutBlack = 64.0f;
    private float mInWhite = 191.0f;
    private float mOutWhite = 191.0f;
    private float mSaturation = 1.0f;

    public Matrix3f satMatrix = new Matrix3f();
    float mInWMinInB;
    float mOutWMinOutB;
    float mOverInWMinInB;

    boolean mUseFull;
    boolean mUseV4;

    public Levels(boolean useFull, boolean useV4) {
        mUseFull = useFull;
        mUseV4 = useV4;
    }


    private void setLevels() {
        mInWMinInB = mInWhite - mInBlack;
        mOutWMinOutB = mOutWhite - mOutBlack;
        mOverInWMinInB = 1.f / mInWMinInB;

        mScriptR.set_inBlack(mInBlack);
        mScriptR.set_outBlack(mOutBlack);
        mScriptR.set_inWMinInB(mInWMinInB);
        mScriptR.set_outWMinOutB(mOutWMinOutB);
        mScriptR.set_overInWMinInB(mOverInWMinInB);
        mScriptF.set_inBlack(mInBlack);
        mScriptF.set_outBlack(mOutBlack);
        mScriptF.set_inWMinInB(mInWMinInB);
        mScriptF.set_outWMinOutB(mOutWMinOutB);
        mScriptF.set_overInWMinInB(mOverInWMinInB);
    }

    private void setSaturation() {
        float rWeight = 0.299f;
        float gWeight = 0.587f;
        float bWeight = 0.114f;
        float oneMinusS = 1.0f - mSaturation;

        satMatrix.set(0, 0, oneMinusS * rWeight + mSaturation);
        satMatrix.set(0, 1, oneMinusS * rWeight);
        satMatrix.set(0, 2, oneMinusS * rWeight);
        satMatrix.set(1, 0, oneMinusS * gWeight);
        satMatrix.set(1, 1, oneMinusS * gWeight + mSaturation);
        satMatrix.set(1, 2, oneMinusS * gWeight);
        satMatrix.set(2, 0, oneMinusS * bWeight);
        satMatrix.set(2, 1, oneMinusS * bWeight);
        satMatrix.set(2, 2, oneMinusS * bWeight + mSaturation);
        mScriptR.set_colorMat(satMatrix);
        mScriptF.set_colorMat(satMatrix);
    }

    @Override
    public void onSaturationBarChanged(int progress) {
        mSaturation = (float)progress / 50.0f;
        setSaturation();
    }
    @Override
    public void onContrastBarChanged(int progress) {
        mInBlack = (float)progress;
        setLevels();
    }
    @Override
    public void onBrightnessBarChanged(int progress) {
        mOutWhite = (float)progress + 127.0f;
        setLevels();
    }

    @Override
    public void createEffect(android.content.res.Resources res) {
        mScriptR = new ScriptC_levels_relaxed(mRS);
        mScriptF = new ScriptC_levels_full(mRS);
        setSaturation();
        setLevels();
    }

    @Override
    public void runEffect() {
        if (mUseFull) {
            if (mUseV4) {
                mScriptF.forEach_root4(mInPixelsAllocation, mOutPixelsAllocation);
            } else {
                mScriptF.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
            }
        } else {
            if (mUseV4) {
                mScriptR.forEach_root4(mInPixelsAllocation, mOutPixelsAllocation);
            } else {
                mScriptR.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
            }
        }
    }
}
