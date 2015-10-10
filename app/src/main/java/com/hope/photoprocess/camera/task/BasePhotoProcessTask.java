package com.hope.photoprocess.camera.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.hope.photoprocess.camera.delegate.PhotoProcessBitmapDelegate;


/**
 * 图片处理Base Task
 *
 * Created by Hope on 15/8/26.
 */
public abstract class BasePhotoProcessTask implements Handler.Callback {

    private Bitmap mSourceBitmap;

    private PhotoProcessBitmapDelegate listener;

    private HandlerThread handlerThread;

    private Handler mHandler = new Handler(this);

    protected static final int HANDLER_CODE_SUCC = 1;
    protected static final int HANDLER_CODE_FAILD = 2;

    private boolean isRun = false;

    private Handler handler;

    public BasePhotoProcessTask() {
        handlerThread = new HandlerThread("life.com");
        handlerThread.start(); // 创建HandlerThread后一定要记得start()

        Looper looper = handlerThread.getLooper();

        handler = new Handler(looper);
    }

    public final void execTask() {
        if(isRun) {
            return;
        }
        isRun = true;
        handler.post(new PostRunnable());
    }

    public void clear() {
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }


    private class PostRunnable implements Runnable {

        @Override
        public void run() {
            sendCallback(doRun(getSourceBitmap()));
        }
    }

    private final void sendCallback(Bitmap bitmap) {
        int resultCode = HANDLER_CODE_FAILD;

        Message msg = Message.obtain();

        if(bitmap != null) {
            resultCode = HANDLER_CODE_SUCC;
            msg.obj = bitmap;
        }

        msg.what = resultCode;

        mHandler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        isRun = false;
        switch (msg.what) {
            case HANDLER_CODE_FAILD:
                if(listener != null) {
                    listener.onCallback(null);
                }
                break;
            case HANDLER_CODE_SUCC:
                if(listener != null) {
                    listener.onCallback((Bitmap) msg.obj);
                }
                break;
        }
        return false;
    }

    public void setListener(PhotoProcessBitmapDelegate listener) {
        this.listener = listener;
    }

    public void setSourceBitmap(Bitmap sourceBitmap) {
        this.mSourceBitmap = sourceBitmap;
    }

    public Bitmap getSourceBitmap() {
        return mSourceBitmap;
    }


    protected abstract Bitmap doRun(Bitmap bitmap);

}

