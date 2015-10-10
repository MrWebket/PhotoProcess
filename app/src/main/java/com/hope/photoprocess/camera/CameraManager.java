package com.hope.photoprocess.camera;

import android.content.Context;
import android.graphics.Bitmap;

import com.hope.photoprocess.app.App;
import com.hope.photoprocess.camera.delegate.PhotoProcessBitmapDelegate;
import com.hope.photoprocess.camera.task.BasePhotoProcessTask;
import com.hope.photoprocess.camera.task.BitmapBlurProcessTask;
import com.hope.photoprocess.camera.task.BitmapLightnessTask;
import com.shenghuoli.library.utils.CollectionUtil;
import com.shenghuoli.library.utils.DisplayUtil;
import com.shenghuoli.library.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 相机管理类
 *
 * @author Hope
 */
public class CameraManager {
    private static CameraManager mInstance;

    private BitmapLightnessTask mBitmapLightnessTask;
    private BitmapBlurProcessTask mBitmapBlurProcessTask;

    private BasePhotoProcessTask mLastExecTask;

    private PhotoProcessBitmapDelegate listener;

    private List<BasePhotoProcessTask> list = new ArrayList<BasePhotoProcessTask>();

    public static CameraManager getInstance() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }

    public static int getCameraAlbumWidth() {
        return (App.getInstance().getScreenWidth() - DisplayUtil.dip2px(App.getInstance(), 10)) / 4 - DisplayUtil.dip2px(App.getInstance(), 4);
    }

    // 相机照片列表高度计算
    public static int getCameraPhotoAreaHeight() {
        return getCameraPhotoWidth() + DisplayUtil.dip2px(App.getInstance(), 4);
    }

    public static int getCameraPhotoWidth() {
        return App.getInstance().getScreenWidth() / 4 - DisplayUtil.dip2px(App.getInstance(), 2);
    }

    public void close() {
        mInstance = null;

        for (int i = 0; i < list.size(); i++) {
            list.get(i).clear();
        }
    }


    private String getSaveSDCardFileName() {
        return System.currentTimeMillis() + ".jpg";
    }

    public String saveBitmapToSDCard(Context context, Bitmap bitmap) {

        return ImageUtil.saveBitmap(context, bitmap, ImageUtil.JPG, App.getInstance().getPhotoPath(), getSaveSDCardFileName(), true);
    }

    private Bitmap mProcessSourceBitmap;

    public void setProcessSourceBitmap(Bitmap sourceBitmap) {
        mProcessSourceBitmap = sourceBitmap;

        for (int i = 0; i < list.size(); i++) {
            list.get(i).releaseResultBitmap();
        }
    }

    /**
     * 调整图片亮度
     *
     * @param progress 亮度幅度0~2
     */
    public void updateLightness(float progress) {
        if(mBitmapLightnessTask == null) {
            mBitmapLightnessTask = new BitmapLightnessTask();
            list.add(mBitmapLightnessTask);
        }
        mBitmapLightnessTask.setSourceBitmap(mProcessSourceBitmap);
        mBitmapLightnessTask.setListener(listener);
        mBitmapLightnessTask.setProgress(progress);

        changeTaskPointer(mBitmapLightnessTask);

        mLastExecTask = mBitmapLightnessTask;

        mBitmapLightnessTask.execTask();
    }

    public void updateBlur(int type, float radius,float mPercentageX, float mPercentageY, boolean isModifySourceBlur) {
        if(mBitmapBlurProcessTask == null) {
            mBitmapBlurProcessTask = new BitmapBlurProcessTask();
            list.add(mBitmapBlurProcessTask);
        }
        mBitmapBlurProcessTask.setListener(listener);
        mBitmapBlurProcessTask.setType(type);
        mBitmapBlurProcessTask.setSourceBitmap(mProcessSourceBitmap);
        mBitmapBlurProcessTask.setRadius(radius);
        mBitmapBlurProcessTask.setPercentageX(mPercentageX);
        mBitmapBlurProcessTask.setPercentageY(mPercentageY);
        mBitmapBlurProcessTask.setModifySourceBlur(isModifySourceBlur);

        changeTaskPointer(mBitmapBlurProcessTask);

        mLastExecTask = mBitmapBlurProcessTask;

        mBitmapBlurProcessTask.execTask();
    }

    private void changeTaskPointer(BasePhotoProcessTask currentTask) {
        if(!CollectionUtil.isEmpty(list) && currentTask != mLastExecTask) {

            for (int i = 0; i < list.size(); i++) {
                BasePhotoProcessTask task = list.get(i);
                if(task == currentTask && mLastExecTask != null) {
                    task.setBaseTask(mLastExecTask);
                    continue;
                }
                if(i == list.size() - 1) {
                    task.setBaseTask(task);
                    continue;
                }
                if(i < list.size() - 1) {
                    task.setBaseTask(list.get(i + 1));
                }
            }
        }
    }

    public void setBitmapLightnessListener(PhotoProcessBitmapDelegate listener) {
        this.listener = listener;
    }

}
