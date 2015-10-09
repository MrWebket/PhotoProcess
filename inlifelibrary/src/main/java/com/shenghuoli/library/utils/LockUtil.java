package com.shenghuoli.library.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * 屏幕锁监听方法<br>
 * 实现原理:<br>
 * 1:监听生命周期的的变化 超过30秒无变化的 那么直接去看看是不是app进入后台了<br>
 * 2:监听电源键的情况  如果屏幕关闭 那么肯定离开app关闭屏幕
 * @author vendor
 */
public class LockUtil implements ScreenObserver.ScreenStateListener {
    
    private static final String TAG = "LockUtils";
    
    /** 最长MAX_WAITMAX_WAIT_CHECK秒无更新 那么开始执行查看是否进入后台 */
    private static final int MAX_WAIT_CHECK = 10000; 
    /** 最长MAX_WAIT_SLEEP秒后打开app */
    private static final int MAX_WAIT_SLEEP = 60000; 
    
    private boolean usingLock = false;
    
    private int appState = TYPE_ACTIVE;  //app当前状态
    
    /** 活动 */
    private static final int TYPE_ACTIVE = 0;
    /** 准备进入休眠 */
    private static final int TYPE_READY_SLEEP = 1;
    /** 已经休眠 */
    private static final int TYPE_SLEEP = 2;
    /** 进入解锁状态 */
    private static final int TYPE_UNLOCK = 3;
    
    private static LockUtil mLockUtils;
    
    private Context mContext;
    
    private static Class<? extends Activity> mStartAct;  //启动页面
    
    private ScreenObserver mObserver;
    
    private Handler mHandler;
    
    private LockUtil(Context context){
        this.mContext = context;
    }
    
    /**
     * 获取实例
     * @param context 上下文对象<br>
     * 此处应传入getApplicationContext或者MainActivity等singletask的context<br>
     * 保证utils的正确运行
     * @return Lock实例
     */
    public static LockUtil getInstance(Context context){
        if(mLockUtils == null){
            mLockUtils = new LockUtil(context);
        }
        
        return mLockUtils;
    }
    
    public static final void initStartActivity(Class<? extends Activity> startAct){
        LockUtil.mStartAct = startAct;
    }
    
    /**
     * 开始锁屏验证
     */
    public void start(){
        usingLock = true;
        
        appState = TYPE_ACTIVE;
        
        if(mObserver == null){
            mObserver = new ScreenObserver(mContext);
        }
        
        mObserver.requestScreenStateUpdate(this);
        
        if(mHandler == null){
            mHandler = new Handler();
        }
    }
    
    /**
     * 暂停锁屏验证<br>
     * 暂停只是使用全局变量判断  如果需要完全关闭 请使用{@link stop()}
     */
    public void pause(){
        usingLock = false;
    }

    /**
     * 停止锁屏验证
     * @param mContext
     */
    public void stop(){
        usingLock = false;
        
        appState = TYPE_ACTIVE;
        
        if(mHandler != null){
            mHandler.removeCallbacks(mLockUtils.runnable);  //关闭定时器
        }
        
        if(mObserver != null){
            mObserver.stopScreenStateUpdate();  //停止监听
        }
    }
    
    /**
     * 正在验证<br>
     * 代码进入验证页面的时候在onCreate的时候记得使用<br>
     * 否则可能出现多次启动的情况
     * @param mContext
     */
    public void verify(){
        appState = TYPE_UNLOCK;
    }
    
    /**
     * 验证成功  重置锁屏
     * @param mContext
     */
    public void reset(){
        appState = TYPE_ACTIVE;
    }
    
    /**
     * 提示工具类 需要更新生命周期<br>
     * 通过handler的定时器的重新开启的方式
     */
    public void updateLifecycle(){
        if(!usingLock){
            Log.i(TAG, "not useing lock");
            return;
        }
        
        Log.i(TAG, "updateLifecycle");
        
        switch (appState) {
        case TYPE_ACTIVE:
        case TYPE_READY_SLEEP:
            appState = TYPE_ACTIVE; //只要有更新了生命周期  只要不是被上一次识别为是TYPE_SLEEP 那么所有的操作都识别为活动
            mHandler.removeCallbacks(runnable);  //关闭定时器
            mHandler.postDelayed(runnable, MAX_WAIT_CHECK);  //延迟启动
            break;
        case TYPE_SLEEP:
            Log.i(TAG, "lock type is sleep");
            
            appState = TYPE_UNLOCK;  //将状态转换为正在解锁状态
            
            //启动锁屏页面
            Intent intent = new Intent(mContext, mStartAct);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        case TYPE_UNLOCK:
            Log.i(TAG, "lock type is unlock");
            break;
        }
    }
    
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            switch (appState) {
            case TYPE_ACTIVE:
                Log.i(TAG, "lock type is active");
                Log.i(TAG, "check app is in the background");
                if(isAppOnBackground(mContext)){  //如果app进入后台
                    Log.i(TAG, "app is in the background");
                    
                    appState = TYPE_READY_SLEEP;
                    
                    mHandler.removeCallbacks(runnable);  //关闭定时器
                    mHandler.postDelayed(runnable, MAX_WAIT_SLEEP);  //开始睡眠倒计时
                }else{
                    Log.i(TAG, "app is not in the background");
                }
                break;
            case TYPE_READY_SLEEP:
                Log.i(TAG, "lock type is ready sleep");
                
                appState = TYPE_SLEEP;  //将状态转换为睡眠状态
                
                mHandler.post(runnable);  //开始进入睡眠
                break;
            case TYPE_SLEEP:
                Log.i(TAG, "lock type is sleep");
                break;
            case TYPE_UNLOCK:
                Log.i(TAG, "lock type is unlock");
                break;
            }
        }
    };

    @Override
    public void onScreenOn() {
        Log.i(TAG, "onScreenOn");
    }

    @Override
    public void onScreenOff() {
        if(!usingLock){
            Log.i(TAG, "not useing lock");
            return;
        }
        
        Log.i(TAG, "onScreenOff");
        
        switch (appState) {
        case TYPE_ACTIVE:
        case TYPE_READY_SLEEP:
            appState = TYPE_READY_SLEEP;
            
            mHandler.removeCallbacks(runnable);  //关闭定时器
            mHandler.postDelayed(runnable, MAX_WAIT_SLEEP);  //开始睡眠倒计时
            break;
        default:
            Log.i(TAG, "app is deblocking");
            break;
        }
    }
    
    /**
     * 程序是否在后台运行
     * @return
     */
    private boolean isAppOnBackground(Context context) {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null){
            return true;
        }

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false;
            }
        }

        return true;
    }
}
