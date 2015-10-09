
package com.shenghuoli.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.inputmethod.InputMethodManager;

public class AppUtil {
    private AppUtil() {
        
    }

    /**
     * 隐藏输入框
     */
    public static void hideSoftInput(Context context, View view) {
        if (view != null && view.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context);
        if (info != null) {
            return info.versionCode;
        }
        return -1;
    }

    /**
     * 获取包信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info;
        } catch (NameNotFoundException e) {
        }
        return null;
    }
    
    /**
     * 获取版本名称
     * 
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
    	 PackageInfo info = getPackageInfo(context);
         if (info != null) {
             return info.versionName;
         }
         return "";
    }
    
    /**
     * 获取手机IMEI
     * 
     * @param context
     * @return
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
    
    /**
     * 获取屏幕宽度
     * 
     * @param mActivity
     * @return
     */
    public static int getScreenWidth(Activity mActivity) {
    	DisplayMetrics metric = new DisplayMetrics();
    	mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;     // 屏幕宽度（像素）
    }
    
    /**
     * 获取屏幕高度
     * 
     * @param mActivity
     * @return
     */
    public static int getScreenHeight(Activity mActivity) {
    	DisplayMetrics metric = new DisplayMetrics();
    	mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;     // 屏幕宽度（像素）
    }
    
    /**
     * 获取屏幕截图
     * 
     * @param activity
     * @return
     */
	public static Bitmap getScreenShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
 
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        // 获取屏幕宽和高
        int widths = getScreenWidth(activity);
        int heights = getScreenHeight(activity);
 
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
 
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeights, widths, heights - statusBarHeights);
 
        // 销毁缓存信息
        view.destroyDrawingCache();
 
        return bmp;
    }
	
	/**
	 * 将View转换为 Bitmap 对象
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view){
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	    view.buildDrawingCache();
	    Bitmap bitmap = view.getDrawingCache();

	    return bitmap;
	}
	

}
