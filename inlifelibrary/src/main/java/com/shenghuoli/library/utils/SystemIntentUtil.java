
package com.shenghuoli.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * 跳转到系统intent的工具类
 * 
 * @author dbzhuang
 */
public class SystemIntentUtil {
    private SystemIntentUtil() {
    }

    /**
     * 跳转到拨号界面
     * 
     * @param number：要拨打的电话号码
     */
    public static void gotoDailUI(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        try {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            context.startActivity(phoneIntent);
        } catch (Exception e) {
        }
    }
    /**
     * 调用系统相机
     * @param activity
     * @param filePath：图片要保存的路径
     * @param requestCode：请求码
     */
    public static void takePicture(Activity activity, String filePath, int requestCode) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
        }
    }
    
    /**
     * 跳转至联系人列表
     * 
     * @param context
     * @param requestCode
     */
    public static void gotoContactsList(Activity activity,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }
}
