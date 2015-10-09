package com.shenghuoli.library.activitys;

import android.app.Activity;
import android.app.Application;

import com.shenghuoli.library.utils.CrashHandler;
import com.shenghuoli.library.utils.LogUtil;
import com.vendor.library.utils.imageloader.cache.disc.impl.UnlimitedDiscCache;
import com.vendor.library.utils.imageloader.core.ImageLoader;
import com.vendor.library.utils.imageloader.core.ImageLoaderConfiguration;
import com.vendor.library.utils.imageloader.core.assist.QueueProcessingType;
import com.vendor.library.utils.imageloader.utils.StorageUtils;

import java.util.Stack;

/**
 * 类描述：具体应该的Application的基类,提供应用发生异常时的处理操作：日志记录和界面关闭、杀死进程等操作
 */
public abstract class BaseApplication extends Application implements CrashHandler.UncaughtExceptionHanlderListener {

    // activity创建的记录，用于异常时的关闭处理
    private Stack<Activity> mActivities = new Stack<Activity>();
    
    private int hardCacheSize = (int) (Runtime.getRuntime().maxMemory()) / 8;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        initImageLoader();
        
        CrashHandler.getInstance(getApplicationContext()).setHanlderListener(this);
        CrashHandler.getInstance(getApplicationContext()).setCrashLogDir(getCrashLogDir());
        
        LogUtil.init(isDebugModel(), "Life");
        init();
    }
    
    /**
     * 初始化图片下载类
     */
    private void initImageLoader(){
        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .diskCache(new UnlimitedDiscCache(StorageUtils.getCacheDirectory(getApplicationContext())))
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .memoryCacheSize(hardCacheSize)
        .memoryCacheSizePercentage(75)
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .diskCacheSize(50 * 1024 * 1024)
        .diskCacheFileCount(300)
        .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void handlerUncaughtException() {
        exitApplication();
    }


    /**
     * 具体Application的初始化操作
     */
    protected abstract void init();

    /**
     * 具体Application的销毁需要做的操作，比如数据库关闭等
     */
    protected abstract void onDestory();
    
    /**
     * 是否是debug开发模式，默认为true
     * @return
     */
    protected abstract boolean isDebugModel();
    
    /**
     * 设置crash log路径
     * @return
     */
    protected abstract String getCrashLogDir();
    /**
     * 获取本地sd卡根目录
     * @return
     */
    protected abstract String getRootDir();

    /**
     * 当一个activity创建时，将activity push到activity记录栈顶
     * 
     * @param activity
     */
    public void pushActivity(Activity activity) {
        mActivities.push(activity);
    }
    
    /**
     * 判断是否包含这个activity
     * @param cls
     * @return
     */
    public boolean containActivity(Class<?> cls){
        if(mActivities == null){
            return false;
        }
        
        for(Activity act : mActivities){
            if(act.getClass().equals(cls)){
                return true;
            }
        }
        
        return false;
    }

    /**
     * 当一个activity销毁时，从activity记录栈移除相应的activity
     * 
     * @param activity
     */
    public void removeActivity(Activity activity) {
        int index = mActivities.indexOf(activity);
        if (index != -1) {
            mActivities.remove(index);
        }
    };

    // 关闭所有activity，如果它们存在
    private void finishAllActivityIfExist() {
        if (mActivities != null) {
            while (!mActivities.isEmpty()) {
                mActivities.remove(mActivities.size() - 1).finish();
            }
        }
    }

    /**
     * 退出应用
     */
    public void exitApplication() {
//      isRunning = true;
        
        onDestory();
        finishAllActivityIfExist();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
