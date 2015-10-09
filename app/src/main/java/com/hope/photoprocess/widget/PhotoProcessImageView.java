package com.hope.photoprocess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.shenghuoli.android.camera.delegate.PhotoProcessImageViewDelegate;

/**
 * 图片处理ImageView
 *
 */
public class PhotoProcessImageView extends ImageView {

    private static final String TAG = PhotoProcessImageView.class.getSimpleName();

    private boolean isSupportblur = true;

    private Bitmap mBitmap;

    private int width, height;

    private Paint mPaint;

    private Paint mBlur;

    private int mCoverWidth, mCoverHeight;

    private PhotoProcessImageViewDelegate mDelegate;

    public PhotoProcessImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public PhotoProcessImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PhotoProcessImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPaint = new Paint();
        this.mPaint.setAntiAlias(true); // 消除锯齿
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;

        super.setImageBitmap(bm);
    }

    public int getCalculateWidth() {
        return width;
    }

    public int getCalculateHeight() {
        return height;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(width == 0) {
            width = getWidth();
            height = getHeight();
        }
    }

    private int mLastUpX;
    private int mLastUpY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                mLastUpX = (int) event.getX();
                mLastUpY = (int) event.getY();
                if(isSupportblur && mDelegate != null && mLastUpX !=0 && mLastUpY != 0) {
                    mDelegate.onProcessAtLocation(mLastUpX, mLastUpY);
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setSupportblur(boolean isSupportblur) {
        this.isSupportblur = isSupportblur;
    }


    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public void setDelegate(PhotoProcessImageViewDelegate delegate) {
        this.mDelegate = delegate;
    }
}
