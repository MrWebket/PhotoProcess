package com.hope.photoprocess.camera.task;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;


/**
 * 处理图片亮度
 *
 * Created by Hope on 15/8/24.
 */
public class BitmapLightnessTask extends BasePhotoProcessTask  {

    private ColorMatrix mLightnessMatrix;
    private ColorMatrix mAllMatrix;

    private float mProgress;

    /**
     * 图片亮度调节是否正在执行
     */
    private boolean isRun = false;

    private float mLastHightnessProgress;

    public float getProgress() {
        return this.mProgress;
    }

    public void setProgress(float progress) {
        if(isRun) {
            mLastHightnessProgress = progress;
            return;
        }
        this.mProgress = progress;
    }

    @Override
    protected Bitmap doRun(Bitmap sourceBitmap) {
        try {
            if(sourceBitmap == null) {
                return null;
            }
            if (null == mLightnessMatrix) {
                mLightnessMatrix = new ColorMatrix(); // 用于颜色变换的矩阵，android位图颜色变化处理主要是靠该对象完成
            }

            Bitmap bmp = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);

            mLightnessMatrix.reset();
            mLightnessMatrix.setScale(mProgress, mProgress, mProgress, 1); // 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考

            // 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
            Canvas canvas = new Canvas(bmp); // 得到画笔对象
            Paint paint = new Paint(); // 新建paint
            paint.setAntiAlias(true); // 设置抗锯齿,也即是边缘做平滑处理

            if (null == mAllMatrix) {
                mAllMatrix = new ColorMatrix();
            }

            mAllMatrix.reset();
            mAllMatrix.postConcat(mLightnessMatrix); // 效果叠加

            paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// 设置颜色变换效果
            canvas.drawBitmap(sourceBitmap, 0, 0, paint); // 将颜色变化后的图片输出到新创建的位图区
            // 返回新的位图，也即调色处理后的图片

            if(mLastHightnessProgress!=0 && mLastHightnessProgress != mProgress) { //说明有新的需要
                mProgress = mLastHightnessProgress;

                execTask();
            }
            return bmp;

        }catch (OutOfMemoryError e) {
            System.gc();
            return null;
        }
    }


}
