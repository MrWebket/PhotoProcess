package com.hope.photoprocess.app;

import android.os.Environment;
import android.util.DisplayMetrics;

import com.hope.photoprocess.contants.AppConfig;
import com.shenghuoli.library.activitys.BaseApplication;
import com.shenghuoli.library.utils.SDCardUtil;

import java.io.File;

/**
 * 应用程序入口
 */
public class App extends BaseApplication {
    
    private static App sInstance;

    private DisplayMetrics displayMetrics = null;

    @Override
    protected void init() {
        sInstance = this;
    }

    /**
     * 获取app实例
     * 
     * @return
     */
    public static App getInstance() {
        if (sInstance == null) {
            sInstance = new App();
        }

        return sInstance;
    }
    


    @Override
    protected void onDestory() {


        sInstance = null;
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    /**
     * 获取保存拍照照片的文件路径
     *
     * @return
     */
    public String getPhotoPath() {
        StringBuffer sb = new StringBuffer();

        if (getRootDir() != null) {
            sb.append(getRootDir());
            sb.append(File.separator);
        } else {
            sb.append(File.separator);
            sb.append("sdcard");
            sb.append(getPackageName());
            sb.append(File.separator);
        }

        sb.append(AppConfig.PHOTO_PATH);

        return sb.toString();
    }

    @Override
    protected String getCrashLogDir() {
        StringBuffer sb = new StringBuffer();

        if (getRootDir() != null) {
            sb.append(getRootDir());
            sb.append(File.separator);
        } else {
            sb.append(File.separator);
            sb.append("sdcard");
            sb.append(getPackageName());
            sb.append(File.separator);
        }

        sb.append("log");

        return sb.toString();
    }

    @Override
    protected boolean isDebugModel() {
        // 在生产模式的时候不开启
        return !AppConfig.PRODUCTION_MODEL;
    }

    @Override
    protected String getRootDir() {
        if (SDCardUtil.isSDCardAvaiable()) {
            StringBuffer sb = new StringBuffer();
            sb.append(Environment.getExternalStorageDirectory().getPath());
            sb.append(File.separator);
            sb.append(getPackageName());
            
            return sb.toString();
        }
        
        return null;
    }

}
