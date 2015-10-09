package com.hope.photoprocess.fragment;

import com.hope.photoprocess.camera.delegate.PhotoProcessFragmentDelegate;
import com.shenghuoli.library.activitys.BaseFragment;

/**
 * 图片处理
 * Created by joe on 15/8/21.
 */
public abstract class PhotoProcessBaseFragment extends BaseFragment {

    protected PhotoProcessFragmentDelegate mDelegate;

    public void setPhotoProcessDelegate(PhotoProcessFragmentDelegate delegate) {
        this.mDelegate = delegate;
    }

    /**
     * 当back键按下时候的响应。
     *
     * @return 是否被响应，如果返回true，则是被响应。如果返回false，则不被响应。
     */
    public boolean onBackPressed() {
        return false;
    }
}
