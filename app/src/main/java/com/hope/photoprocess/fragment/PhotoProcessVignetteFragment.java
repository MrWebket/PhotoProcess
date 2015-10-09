package com.hope.photoprocess.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.shenghuoli.android.R;
import com.shenghuoli.android.camera.PhotoProcessBaseFragment;

/**
 * 暗角
 * Created by Hope on 15/9/8.
 */
public class PhotoProcessVignetteFragment extends PhotoProcessBaseFragment implements SeekBar.OnSeekBarChangeListener {
    /**
     * SeekBar的最大值
     */
    private static final int MAX_VALUE = 255;

    /** SeekBar的中间值 */
    private static final int MIDDLE_VALUE = MAX_VALUE / 2 + 1;

    private SeekBar mSeekBar;

    private float mCurrentBrightness = 0.5f;

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, String tag) {
        return inflater.inflate(R.layout.photo_process_brightness_fragment, container, false);
    }

    @Override
    protected void findView() {
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setMax(MAX_VALUE);
        mSeekBar.setProgress(MIDDLE_VALUE);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void initialize() {

    }

    public void resetCurrentProgress() {
        mCurrentBrightness = 0.5f;
        mSeekBar.setProgress(MIDDLE_VALUE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mCurrentBrightness = (float) progress / (float)MAX_VALUE;

            if(mDelegate != null) {
                mDelegate.onPhotoProcessCallback(this, mCurrentBrightness);
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
