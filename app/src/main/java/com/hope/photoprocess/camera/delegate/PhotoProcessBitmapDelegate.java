package com.hope.photoprocess.camera.delegate;

import android.graphics.Bitmap;

/**
 * 图片处理,bitmap代理
 *
 * Created by Hope on 15/8/26.
 */
public interface PhotoProcessBitmapDelegate {

    public void onCallback(Bitmap bitmap);
}
