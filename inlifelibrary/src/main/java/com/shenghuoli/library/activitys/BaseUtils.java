
package com.shenghuoli.library.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.shenghuoli.library.R;
import com.shenghuoli.library.constants.BaseConstants;

/**
 * 基于Activity或者Fragment扩展的广播实现
 * 
 * @author Hope
 */
public class BaseUtils {

    private static final String BASE_TAG = "BaseUtils";

    /**
     * 退出的请求类</br> 广播形式实现全部activity的退出操作
     * 
     * @param context Context实体
     */
    public static final void requestExit(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Intent intent = new Intent();
            intent.setAction(info.packageName + BaseConstants.BROADCASE_ADDRESS);
            intent.putExtra(BaseConstants.BROADCASE_INTENT, BaseConstants.BROADCASE_INTENT_EXIT);
            context.sendBroadcast(intent);
        } catch (NameNotFoundException e) {
            Log.e(BASE_TAG, e.getMessage());
        }
    }

    /**
     * 发送一个广播
     * 
     * @param value
     */
    public static final void sendBroadcast(Context context, int value) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Intent intent = new Intent();
            intent.setAction(info.packageName + BaseConstants.BROADCASE_ADDRESS);
            intent.putExtra(BaseConstants.BROADCASE_INTENT, value);
            context.sendBroadcast(intent);
        } catch (NameNotFoundException e) {
            Log.e(BASE_TAG, e.getMessage());
        }
    }
    
    /**
     * 开启加载中的动画
     * @param context
     * @param progressImage
     */
    public static void showProgressAnimation(Context context, ImageView progressImage){
        if(progressImage == null){
            return;
        }
        
        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_progress);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        progressImage.startAnimation(operatingAnim);
    }
    
    /**
     * 关闭加载中的动画
     * @param context
     * @param progressImage
     */
    public static void dismissProgressAnimation(ImageView progressImage){
        if(progressImage == null){
            return;
        }
        
        progressImage.clearAnimation();
    }
    
    
}
