package com.hope.photoprocess.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.hope.photoprocess.R;
import com.hope.photoprocess.camera.CameraManager;
import com.hope.photoprocess.camera.delegate.OnPhotoProessControlDelegate;
import com.hope.photoprocess.camera.delegate.PhotoProcessBitmapDelegate;
import com.hope.photoprocess.camera.delegate.PhotoProcessFragmentDelegate;
import com.hope.photoprocess.fragment.PhotoProcessAdjustmentFragment;
import com.hope.photoprocess.fragment.PhotoProcessBaseFragment;
import com.hope.photoprocess.fragment.PhotoProcessBrightnessFragment;
import com.hope.photoprocess.fragment.PhotoProcessFilterFragment;
import com.hope.photoprocess.fragment.PhotoProcessTemplateFragment;
import com.hope.photoprocess.fragment.PhotoProcessVignetteFragment;
import com.hope.photoprocess.widget.PhotoProcessImageView;
import com.hope.photoprocess.widget.PhotoProessControlView;
import com.shenghuoli.library.activitys.BaseFragmentActivity;
import com.shenghuoli.library.utils.FileUtil;

/**
 * 图片处理Activity
 *
 * Created by Hope on 15/8/20.
 */
public class PhotoProcessActivity extends BaseFragmentActivity implements PhotoProcessFragmentDelegate, View.OnClickListener,
        OnPhotoProessControlDelegate, PhotoProcessBitmapDelegate {

    private static final int TAB_TEMPLATE = 1;
    private static final int TAB_GPU = 2;
    private static final int TAB_ADJUSTMENT = 3;
    private static final int TAB_BRIGHTNESS = 4;
    private static final int TAB_VIGNETTE = 5;

    public static final String EXTRA_FILE_PATH = "extra:filePath";

    private String filePath;

    private PhotoProcessImageView mProcessImage;

    private FragmentTransaction transaction;

    private Fragment mFragment = null;

    /**
     * 模板
     */
    private PhotoProcessTemplateFragment mTemplateFragment;
    /**
     * 滤镜
     */
    private PhotoProcessFilterFragment mFilterFragment;
    /**
     * 调整
     */
    private PhotoProcessAdjustmentFragment mAdjustmentFragment;
    /**
     * 亮度调节
     */
    private PhotoProcessBrightnessFragment mBrightnessFragment;
    /**
     * 暗角
     */
    private PhotoProcessVignetteFragment mVignetteFragment;

    private Bitmap mSourceBtimap;
    private Bitmap mCurrentBitmap;

    private CameraManager mCameraManager;

    private PhotoProessControlView mPhotoProessControlView;

    private ImageView mVignetteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState, String tag) {
        setContentView(R.layout.photo_process);
    }

    @Override
    protected void findView() {
        mVignetteImage = (ImageView) findViewById(R.id.vignette_image);
        mVignetteImage.getBackground().setAlpha(0);

        mPhotoProessControlView = (PhotoProessControlView) findViewById(R.id.photo_proess_controll_view);
        mPhotoProessControlView.setDelegate(this);

        findViewById(R.id.template_btn).setOnClickListener(this);
        findViewById(R.id.filter_btn).setOnClickListener(this);
        findViewById(R.id.adjustment_btn).setOnClickListener(this);

        mProcessImage = (PhotoProcessImageView) findViewById(R.id.process_image);
    }

    @Override
    protected void initialize() {
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_FILE_PATH)) {
            finish();
            return;
        }
        filePath = extras.getString(EXTRA_FILE_PATH);

        mSourceBtimap = BitmapFactory.decodeFile(filePath).copy(Bitmap.Config.ARGB_8888, true);;
        mProcessImage.setImageBitmap(mSourceBtimap);

        mCameraManager = CameraManager.getInstance();
        mCameraManager.setBitmapLightnessListener(this);
        mCameraManager.setProcessSourceBitmap(mSourceBtimap);

        replaceView(TAB_TEMPLATE);
    }

    /**
     * 切换视图
     *
     * @param currItem
     */
    public synchronized void replaceView(int currItem) {
        transaction = getSupportFragmentManager().beginTransaction();

        if (mFragment != null) {
            transaction.hide(mFragment);
        }

        switch (currItem) {
            case TAB_TEMPLATE:
                if (mTemplateFragment == null) {
                    mTemplateFragment = new PhotoProcessTemplateFragment();
                    mTemplateFragment.setPhotoProcessDelegate(this);

                    transaction.add(R.id.frameLayout, mTemplateFragment);
                }

                mFragment = mTemplateFragment;
                break;
            case TAB_GPU:
                if (mFilterFragment == null) {
                    mFilterFragment = new PhotoProcessFilterFragment();
                    mFilterFragment.setPhotoProcessDelegate(this);
                    transaction.add(R.id.frameLayout, mFilterFragment);
                }

                mFilterFragment.setBitmap(mSourceBtimap);

                mFragment = mFilterFragment;
                break;
            case TAB_ADJUSTMENT:
                if (mAdjustmentFragment == null) {
                    mAdjustmentFragment = new PhotoProcessAdjustmentFragment();
                    mAdjustmentFragment.setPhotoProcessDelegate(this);
                    transaction.add(R.id.frameLayout, mAdjustmentFragment);
                }

                mFragment = mAdjustmentFragment;
                break;
            case TAB_BRIGHTNESS:
                if(mBrightnessFragment == null) {
                    mBrightnessFragment = new PhotoProcessBrightnessFragment();
                    mBrightnessFragment.setPhotoProcessDelegate(this);
                    transaction.add(R.id.frameLayout, mBrightnessFragment);
                }
                mFragment = mBrightnessFragment;
                break;
            case TAB_VIGNETTE:
                if(mVignetteFragment == null) {
                    mVignetteFragment = new PhotoProcessVignetteFragment();
                    mVignetteFragment.setPhotoProcessDelegate(this);
                    transaction.add(R.id.frameLayout, mVignetteFragment);
                }
                mFragment = mVignetteFragment;
                break;
        }

        transaction.show(mFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.template_btn: //水印
                break;
            case R.id.filter_btn: //滤镜
                replaceView(TAB_GPU);
                break;
            case R.id.adjustment_btn: //调整
                replaceView(TAB_ADJUSTMENT);
                break;
        }
    }

    public void onControlScale(int type,float radius,float lastX, float lastY){
        mCameraManager.updateBlur(type, radius, lastX, lastY , true);
    }

    @Override
    public void onPhotoProcessCallback(PhotoProcessBaseFragment fragment, Object... result) {
        if(fragment instanceof PhotoProcessFilterFragment && mFilterFragment != null) { //滤镜
            int index = Integer.parseInt(String.valueOf(result[0]));

            mCurrentBitmap = mFilterFragment.createGPUImageBitmap(this, mSourceBtimap, index);

            if(mBrightnessFragment != null) {
                mBrightnessFragment.resetCurrentProgress();
            }
            mProcessImage.setImageBitmap(getBitmap());

            mCameraManager.setProcessSourceBitmap(mCurrentBitmap);
        } else if(fragment instanceof PhotoProcessAdjustmentFragment && mAdjustmentFragment != null) { //调整
            int tab = Integer.parseInt(String.valueOf(result[0]));

            switch (tab) {
                case PhotoProcessAdjustmentFragment.TAB_VIGNETTE:
                    replaceView(TAB_VIGNETTE);
                    break;
                case PhotoProcessAdjustmentFragment.TAB_BRIGHTNESS:
                    replaceView(TAB_BRIGHTNESS);
                    break;
            }
        } else if(fragment instanceof PhotoProcessBrightnessFragment) { //亮度
            float currentProgress = Float.parseFloat(String.valueOf(result[0]));
            mCameraManager.updateLightness(currentProgress);
        } else if(fragment instanceof  PhotoProcessVignetteFragment) { //暗角
            float currentProgress = Float.parseFloat(String.valueOf(result[0]));
            mVignetteImage.getBackground().setAlpha((int)(currentProgress * 255));
        }
    }

    @Override
    protected void onDestroy() {
        mProcessImage.setImageBitmap(null);
        if(mCurrentBitmap != null) {
            mCurrentBitmap.recycle();
        }
        if(mSourceBtimap != null) {
            mSourceBtimap.recycle();
        }
        mCameraManager.close();

        FileUtil.deleteFile(filePath);

        super.onDestroy();
    }

    private  Bitmap getBitmap() {
        return mCurrentBitmap == null ? mSourceBtimap : mCurrentBitmap;
    }

//    @Override
//    public void onProcessAtLocation(float radius,int lastX, int lastY) {
//        mCameraManager.updateBlur(radius,(float)lastX / (float)mProcessImage.getCalculateWidth(), (float)lastY / (float)mProcessImage.getCalculateHeight());
//    }

    @Override
    public void onCallback(Bitmap bitmap) {
        if(bitmap != null) {
            mProcessImage.setImageBitmap(bitmap);
        }
    }
}

