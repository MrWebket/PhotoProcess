package com.hope.photoprocess.camera.delegate;


import com.hope.photoprocess.fragment.PhotoProcessBaseFragment;

/**
 * 图片处理Fragment代理
 *
 * Created by joe on 15/8/21.
 */
public interface PhotoProcessFragmentDelegate {

    public void onPhotoProcessCallback(PhotoProcessBaseFragment fragment, Object... result);
}
