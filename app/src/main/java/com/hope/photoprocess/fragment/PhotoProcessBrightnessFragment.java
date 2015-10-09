package com.hope.photoprocess.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.hope.photoprocess.R;


/**
 * 亮度调节
 *
 * Created by Hope on 15/9/8.
 */
public class PhotoProcessBrightnessFragment  extends PhotoProcessBaseFragment implements SeekBar.OnSeekBarChangeListener {

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
            mCurrentBrightness = getAdjustmentValue(progress);

            if(mDelegate != null) {
                mDelegate.onPhotoProcessCallback(this, mCurrentBrightness);
            }
        }
    }

    private float getAdjustmentValue(int progress) {
        return  ((float)0.25+((float)progress/(float)(MAX_VALUE - 0))*((float)0.75-(float)0.25)) * 2;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
