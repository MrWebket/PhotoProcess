package com.hope.photoprocess.camera.task;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;

import com.shenghuoli.android.app.App;
import com.shenghuoli.android.util.FastBlur;
import com.shenghuoli.android.widget.PhotoProessControlView;
import com.shenghuoli.library.utils.DisplayUtil;

/**
 * 移轴功能Task
 *
 * Created by Hope on 15/8/26.
 */
public class BitmapBlurProcessTask extends BasePhotoProcessTask {

    private static final String TAG = BitmapBlurProcessTask.class.getSimpleName();

    private Bitmap mBlurBitmap;

    private Xfermode mPorterDuffXfermode;

    private Paint mBlurPaint;

    private float mPercentageX;
    private float mPercentageY;

    private boolean isRun = false;

    private boolean isModifySourceBlur = false;

    private float radius;

    private int type;

    private float mLastmPercentageX, mLastmPercentageY, mLastRadius;
    private int mLastType;

    public BitmapBlurProcessTask() {
        super();

        // 初始化混合模式
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        mBlurPaint = new Paint();
        mBlurPaint.setAntiAlias(true);
        mBlurPaint.setXfermode(mPorterDuffXfermode);

        radius = DisplayUtil.dip2px(App.getInstance(), 60);
    }

    public void setModifySourceBlur(boolean isModifySourceBlur) {
        this.isModifySourceBlur = isModifySourceBlur;
    }

    public boolean getModifySourceBlur() {
        return isModifySourceBlur;
    }

    public void setPercentageX(float percentageX) {
        if(isRun) {
            mLastmPercentageX = percentageX;
            return;
        }
        this.mPercentageX = percentageX;
    }

    public void setPercentageY(float percentageY) {
        if(isRun) {
            mLastmPercentageY = percentageY;
            return;
        }
        this.mPercentageY = percentageY;
    }

    public void setRadius(float radius) {
        if(isRun) {
            mLastRadius = radius;
            return;
        }
        this.radius = radius;
    }

    public void setType(int type) {
        if(isRun) {
            mLastType = type;
            return;
        }
        this.type = type;
    }

    @Override
    protected Bitmap doRun(Bitmap sourceBitmap) {

       try {
           getmBlurBitmap(sourceBitmap);

           Bitmap resultBitmap = mBlurBitmap.copy(Bitmap.Config.ARGB_8888, true);
           Canvas canvas = new Canvas(resultBitmap);
           canvas.save();

           // 初始化混合模式
           mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
           mBlurPaint.setXfermode(mPorterDuffXfermode);


           float convertX = (float) mBlurBitmap.getWidth() * mPercentageX;
           float convertY = (float) mBlurBitmap.getHeight() * mPercentageY;

           switch (type) {
               case PhotoProessControlView.CONTROL_TYPE_CIRCLE:
                   canvas.drawCircle(convertX,convertY, radius * mBlurBitmap.getWidth() , mBlurPaint);
                   break;
               case PhotoProessControlView.CONTROL_TYPE_RECT:
                   float bottom = convertY + radius * mBlurBitmap.getHeight();
                   float top = Math.min(mBlurBitmap.getHeight(), convertY - radius * mBlurBitmap.getHeight());
                   canvas.drawRect(0, top, mBlurBitmap.getWidth(), bottom, mBlurPaint);
                   break;
           }

           // 初始化混合模式
           mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
           mBlurPaint.setXfermode(mPorterDuffXfermode);

           canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), mBlurPaint);

           canvas.restore();


           if(mLastmPercentageX !=0 && mLastmPercentageY !=0 && mLastmPercentageX != mPercentageX && mLastmPercentageY != mPercentageY) { //说明有新的需要
               mPercentageX = mLastmPercentageX;
               mPercentageY = mLastmPercentageY;
               execTask();
           }
           return resultBitmap;
       } catch (OutOfMemoryError error) {
           return null;
       }

    }

    private Bitmap getmBlurBitmap(Bitmap sourceBitmap) throws OutOfMemoryError {
        if(isModifySourceBlur || mBlurBitmap == null) {
            if(sourceBitmap.getWidth() > 720 && sourceBitmap.getHeight() > 1280) {

                Bitmap bitmap = Bitmap.createScaledBitmap(sourceBitmap, DisplayUtil.dip2px(App.getInstance(), 30),
                        DisplayUtil.dip2px(App.getInstance(), 60), true);

                mBlurBitmap = FastBlur.doBlur(bitmap, 10, false);

                bitmap.recycle();
            } else {
                mBlurBitmap = FastBlur.doBlur(sourceBitmap, 10, false);
            }

        }
        return mBlurBitmap;
    }



}
