package com.hope.photoprocess.camera.delegate;

/**
 * 图片处理--缩放控件代理
 *
 * Created by Hope on 15/8/26.
 */
public interface OnPhotoProessControlDelegate {

    public void onControlScale(int type, float radius, float lastX, float lastY);
}
