package com.hope.photoprocess.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hope.photoprocess.camera.delegate.OnPhotoProessControlDelegate;
import com.hope.photoprocess.common.SendAuthCodeCommon;
import com.shenghuoli.library.utils.DisplayUtil;
import com.shenghuoli.library.utils.GeometryUtil;

/**
 * 图片处理控制View
 *
 * Created by Hope on 15/8/26.
 */
public class PhotoProessControlView extends View {

    private static final float ANIM_SCALE = 0.3f;

    private static final String TAG = PhotoProessControlView.class.getSimpleName();

    private static final int TOUCH_STATUS_NONE = 0;
    private static final int TOUCH_STATUS_DOUBLE = 1;
    private static final int TOUCH_STATUS_DOWN = 2;
    private static final int TOUCH_STATUS_MOVE = 3;
    private static final int TOUCH_STATUS_CANCEL = 4;
    private static final int TOUCH_STATUS_HIDE = 5;

    public final static int DURATION = 300;

    public static final  int CONTROL_TYPE_CIRCLE = 1;
    public static  final int CONTROL_TYPE_RECT = 2;

    private OnPhotoProessControlDelegate mDelegate;

    protected float mScale = 1;

    private int mTouchStatus;

    protected float mTouchX2, mTouchY2, mTouchX1, mTouchY1, mTouchStartX, mTouchStartY;

    private static final float MIN_SCALE = 0.75f;
    private static final float MAX_SCALE = 1.5f;

    /**
     * 是否拦截  true拦截
     */
    private boolean isInterception = true;

    private int mControlType = CONTROL_TYPE_RECT;

    private int mRadius, mBigRoundRadius;
    private int mRectPaddingSize;

    private Paint mSmallRoundPaint;
    private Paint mBigRoundPaint;

    private static final long ANIM_DURATION = 200;

    private boolean isRunAnim = false;

    private SendAuthCodeCommon mShowControlAnim;
    private SendAuthCodeCommon mHideControlAnim;

    public PhotoProessControlView(Context context) {
        super(context);

        init();
    }

    public PhotoProessControlView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PhotoProessControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setControlType(int controlType) {
        this.mControlType = controlType;
    }

    private void init() {
        mSmallRoundPaint = new Paint();
        mSmallRoundPaint.setAntiAlias(true);
        mSmallRoundPaint.setColor(Color.WHITE);
        mSmallRoundPaint.setStrokeWidth(10);
        mSmallRoundPaint.setStyle(Paint.Style.STROKE);
        mRadius = DisplayUtil.dip2px(getContext(), 60);

        mBigRoundPaint = new Paint();
        mBigRoundPaint.setAntiAlias(true);
        mBigRoundPaint.setColor(Color.WHITE);
        mBigRoundPaint.setStrokeWidth(5);
        mBigRoundPaint.setStyle(Paint.Style.STROKE);

        mBigRoundRadius = mRadius + DisplayUtil.dip2px(getContext(), 40);

        mRectPaddingSize = DisplayUtil.dip2px(getContext(), 70);
    }

    public void setInterception(boolean isInterception) {
        this.isInterception = isInterception;
    }

    public void setDelegate(OnPhotoProessControlDelegate delegate) {
        this.mDelegate = delegate;
    }

    private int width;
    private int height;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(width == 0) {
            width = getWidth();
        }
        if(height == 0) {
            height = getHeight();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isInterception) {
            return super.dispatchTouchEvent(event);
        }
        return false;
    }

    private void setScale(float scale) {
        if (scale > MAX_SCALE) {
            scale = MAX_SCALE;
        } else if (scale < MIN_SCALE) {
            scale = MIN_SCALE;
        }
        mScale = scale;
        invalidate();
    }

    private Point mRoundLocationPoint = new Point();

    private boolean isDrawing = false;

    private int mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x1 = event.getX(0);
        final float y1 = event.getY(0);

        if(isRunAnim) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_2_DOWN:
                mTouchStatus = TOUCH_STATUS_DOUBLE;
                mTouchX2 = event.getX(1);
                mTouchY2 = event.getY(1);
                break;
            case MotionEvent.ACTION_DOWN:

                mTouchStartX = x1;
                mTouchStartY = y1;

                mLastX = 0;
                mLastY = 0;

                if(TOUCH_STATUS_NONE == mTouchStatus) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mTouchStatus != TOUCH_STATUS_DOWN && mTouchStatus != TOUCH_STATUS_MOVE) {
                                startDrawing(x1, y1);
                            }
                        }
                    }, 200);
                }
                mTouchStatus = TOUCH_STATUS_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent Move");

                if (event.getPointerCount() >= 2 && mTouchStatus == TOUCH_STATUS_DOUBLE) {
                    float x2 = event.getX(1);
                    float y2 = event.getY(1);
                    float nowDistance = GeometryUtil.pointDistance(x1, y1, x2, y2);
                    float oldDistance = GeometryUtil.pointDistance(mTouchX1, mTouchY1, mTouchX2, mTouchY2);
                    float scaleRate = nowDistance / oldDistance;
                    float nowScale = scaleRate * mScale;
                    setScale(nowScale);

                    mTouchX2 = x2;
                    mTouchY2 = y2;
                } else {
                    if(Math.abs(Math.abs(x1) - Math.abs(mTouchStartX)) > 5 && mTouchStartX != 0) {

                        int distanceX = (int)(x1 - mLastX);
                        int distanceY = (int)(y1 - mLastY);

                        if(mLastX == 0 && mLastY == 0) {
                            distanceX = 0;
                            distanceY = 0;
                        }

                        mLastX = (int)x1;
                        mLastY = (int)y1;

                        int x = (mRoundLocationPoint.x + distanceX);
                        int y = (mRoundLocationPoint.y + distanceY);

                        mRoundLocationPoint.x = Math.max(0, Math.min(width,x));
                        mRoundLocationPoint.y = Math.max(0, Math.min(height,y));

                        mTouchStatus = TOUCH_STATUS_MOVE;

                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent ACTION_CANCEL");
                if(!isDrawing) {
                    startDrawing(x1, y1);
                } else {
                    handlerCancelTouch();
                }
                break;
        }

        mTouchX1 = x1;
        mTouchY1 = y1;
        return true;
    }

    private float mAnimScale;

    private void startDrawing(final float x,final float y) {
        if(!isDrawing) {

            isDrawing = true;

            mRoundLocationPoint.x = (int)x;
            mRoundLocationPoint.y = (int)y;

            mAnimScale = mScale;

            if(mShowControlAnim == null) {
                mShowControlAnim = new SendAuthCodeCommon(new SendAuthCodeCommon.OnTimingChangeListener() {

                    @Override
                    public void onTimingChange(long milliseconds) {
                        float value = (float) milliseconds / (float) ANIM_DURATION;
                        if(milliseconds == 0) {
                            resetScale();

                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTouchStatus != TOUCH_STATUS_DOUBLE && mTouchStatus != TOUCH_STATUS_MOVE && mTouchStatus != TOUCH_STATUS_CANCEL) {
                                        handlerCancelTouch();
                                    }
                                }
                            }, DURATION);
                        } else {
                            mAnimScale = mScale + ANIM_SCALE * value;

                            mSmallRoundPaint.setAlpha((int) (255 * (1-value)));
                            mBigRoundPaint.setAlpha((int) (255 * (1-value)));

                            isRunAnim = true;
                        }
                        invalidate();
                    }
                });
                mShowControlAnim.setMaxTryAgainTime(ANIM_DURATION);
                mShowControlAnim.setCountDownInterval(10);
            }
            if(!isRunAnim) {
                mShowControlAnim.startCountdown();
            }
        }
    }

    private synchronized void handlerCancelTouch() {
        if(mTouchStatus != TOUCH_STATUS_CANCEL) {
            mTouchStatus = TOUCH_STATUS_CANCEL;

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mTouchStatus == TOUCH_STATUS_CANCEL && !isRunAnim) {

                        mTouchStatus = TOUCH_STATUS_HIDE;

                        mAnimScale = mScale;

                        if (mHideControlAnim == null) {
                            mHideControlAnim = new SendAuthCodeCommon(new SendAuthCodeCommon.OnTimingChangeListener() {

                                @Override
                                public void onTimingChange(long milliseconds) {
                                    float value = (float) milliseconds / (float) ANIM_DURATION;

                                    if (milliseconds == 0) {
                                        mTouchStatus = TOUCH_STATUS_NONE;
                                        invalidate();
                                        resetScale();
                                    } else {
                                        mAnimScale = mScale + (ANIM_SCALE - (ANIM_SCALE * value));

                                        mSmallRoundPaint.setAlpha((int) (255 - 255 * (1 - value)));
                                        mBigRoundPaint.setAlpha((int) (255 - 255 * (1 - value)));

                                        isRunAnim = true;
                                        invalidate();
                                    }

                                }
                            });
                            mHideControlAnim.setMaxTryAgainTime(ANIM_DURATION);
                            mHideControlAnim.setCountDownInterval(10);
                        }
                        if (!isRunAnim) {
                            mHideControlAnim.startCountdown();
                        }
                    }
                }
            }, DURATION);
        }
    }

    private void resetScale() {
        isRunAnim = false;
        mAnimScale = mScale;

        mSmallRoundPaint.setAlpha(255);
        mBigRoundPaint.setAlpha(255);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if(mTouchStatus == TOUCH_STATUS_DOWN || mTouchStatus == TOUCH_STATUS_DOUBLE || mTouchStatus == TOUCH_STATUS_MOVE || mTouchStatus == TOUCH_STATUS_HIDE) {
            switch (mControlType) {
                case CONTROL_TYPE_CIRCLE:
                        switch (mTouchStatus) {
                            case TOUCH_STATUS_HIDE:
                            case TOUCH_STATUS_DOWN:
                                 //带动画效果缩小 || 扩大 半径
                                canvas.drawCircle(mRoundLocationPoint.x, mRoundLocationPoint.y, mRadius * mAnimScale, mSmallRoundPaint);
                                canvas.drawCircle(mRoundLocationPoint.x, mRoundLocationPoint.y, mBigRoundRadius * mAnimScale, mBigRoundPaint);
                                break;
                            default:
                                canvas.drawCircle(mRoundLocationPoint.x, mRoundLocationPoint.y, mRadius * mScale, mSmallRoundPaint);
                                canvas.drawCircle(mRoundLocationPoint.x, mRoundLocationPoint.y, mBigRoundRadius * mScale, mBigRoundPaint);
                                break;
                        }
                        callback();
                        isDrawing = true;

                    break;
                case CONTROL_TYPE_RECT:
                    switch (mTouchStatus) {
                        case TOUCH_STATUS_HIDE:
                        case TOUCH_STATUS_DOWN:
                            //带动画效果缩小 || 扩大 半径
                            canvas.drawLine(0,mRoundLocationPoint.y,width,mRoundLocationPoint.y, mSmallRoundPaint);
                            canvas.drawLine(0, mRoundLocationPoint.y + mRectPaddingSize * mAnimScale, width, mRoundLocationPoint.y + mRectPaddingSize * mAnimScale, mBigRoundPaint);
                            canvas.drawLine(0, mRoundLocationPoint.y - mRectPaddingSize * mAnimScale, width, mRoundLocationPoint.y - mRectPaddingSize * mAnimScale, mBigRoundPaint);
                            break;
                        default:
                            canvas.drawLine(0,mRoundLocationPoint.y,width,mRoundLocationPoint.y, mSmallRoundPaint);
                            canvas.drawLine(0, mRoundLocationPoint.y + mRectPaddingSize * mScale, width, mRoundLocationPoint.y + mRectPaddingSize * mScale, mBigRoundPaint);
                            canvas.drawLine(0, mRoundLocationPoint.y - mRectPaddingSize * mScale, width, mRoundLocationPoint.y - mRectPaddingSize * mScale, mBigRoundPaint);
                            break;
                    }
                    callback();
                    isDrawing = true;
                    break;
            }
        } else {
            isDrawing = false;
        }
    }


    private float getRadiusPercentage() {
       return (mScale * mRadius) / (float) width;
    }

    private float getRectPercentage() {
        return (mScale * (mRectPaddingSize) / (float) height);
    }

    private void callback() {
        if(mDelegate != null) {
            switch (mControlType) {
                case CONTROL_TYPE_CIRCLE:
                    mDelegate.onControlScale(mControlType, getRadiusPercentage(), (float)mRoundLocationPoint.x / (float) width, (float)mRoundLocationPoint.y / (float)height);
                    break;
                case CONTROL_TYPE_RECT:
                    mDelegate.onControlScale(mControlType, getRectPercentage(), (float)mRoundLocationPoint.x / (float) width, (float)mRoundLocationPoint.y / (float)height);
                    break;
            }
        }
    }
}
