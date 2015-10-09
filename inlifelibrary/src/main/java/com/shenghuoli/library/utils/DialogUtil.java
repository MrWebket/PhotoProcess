package com.shenghuoli.library.utils;

import android.R;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 对话框提示
 * @author vendor
 */
public class DialogUtil {
    
    private DialogUtil(){
        
    }
	
	/**
	 * 弹出提示框<br>
	 * 默认一个确定按钮
	 * @param context 上下文对象
	 * @param res 消息内容
	 */
	public static Dialog createDialog(Context context, int res){
		return createDialog(context, res, null);
	}

	/**
	 * 弹出提示框<br>
	 * 默认一个确定按钮
	 * @param context 上下文对象
	 * @param message 消息内容
	 */
	public static Dialog createDialog(Context context, String message){
	    return createDialog(context, message, null);
	}
	
	/**
	 * 弹出提示框<br>
	 * 默认一个确定按钮
	 * @param context 上下文对象
	 * @param res 消息内容
	 * @param positiveListener 确认的按钮的点击监听
	 */
	public static Dialog createDialog(Context context, int res, OnClickListener positiveListener){
	    return createDialog(context, context.getString(res), positiveListener);
	}
	
	/**
	 * 弹出提示框<br>
	 * 默认一个确定按钮
	 * @param context 上下文对象
	 * @param message 消息内容
	 * @param positiveListener 确认的按钮的点击监听
	 */
	public static Dialog createDialog(Context context, final String message, OnClickListener positiveListener){
	    Builder builder = new Builder(context);
	    builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.yes), positiveListener);

		return builder.create();
	}

	/**
	 * 弹出提示框<br>
	 * 包含确定取消按钮
	 * @param context 上下文对象
	 * @param res 消息内容
	 * @param positiveListener  确认的按钮的点击监听
	 * @param negativeListener  取消的按钮的点击监听
	 */
	public static Dialog createDialog(Context context, int res, OnClickListener positiveListener, OnClickListener negativeListener){
		return createDialog(context, context.getString(res), positiveListener, negativeListener);
	}

	/**
	 * 弹出提示框<br>
	 * 包含确定取消按钮
	 * @param context 上下文对象
	 * @param message 消息内容
	 * @param positiveListener  确认的按钮的点击监听
	 * @param negativeListener  取消的按钮的点击监听
	 */
	public static Dialog createDialog(Context context, final String message, OnClickListener positiveListener, OnClickListener negativeListener){
	    Builder builder = new Builder(context);
	    builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.yes), positiveListener);
		builder.setNegativeButton(context.getString(R.string.cancel), negativeListener);

		return builder.create();
	}

	/**
     * 弹出一个列表提示框
     * @param context 上下文对象
     * @param title 消息内容
     * @param items 列表数组
     * @param onClickListener  取消的按钮的点击监听
     * @return
     */
    public static Dialog createDialog(Context context, int title, CharSequence[] items, OnClickListener onClickListener){
        return createDialog(context, context.getString(title), items, onClickListener);
    }

	/**
     * 弹出一个列表提示框
     * @param context 上下文对象
     * @param title 消息内容
     * @param items 列表数组
     * @param onClickListener  取消的按钮的点击监听
     * @return
     */
    public static Dialog createDialog(Context context, String title, CharSequence[] items, OnClickListener onClickListener){
        Builder builder = new Builder(context);
        if(!TextUtils.isEmpty(title)){
            builder.setTitle(title);
        }
        builder.setItems(items, onClickListener);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setCancelable(true);
        return builder.create();
    }

	/**
	 * 弹出自定义提示框<br>
	 * @param context 上下文对象
	 * @param layout view视图
	 */
	public static Dialog createCustomDialog(Context context, View view){
	    Builder builder = new Builder(context);
	    builder.setView(view);
		
		return builder.create();
	}
	
	/**
	 * 弹出一个不可编辑的对话框
	 * @param context 上下文对象
	 * @return
	 */
	public static ProgressDialog createCannotTouchDialog(Context context){
		return createCannotTouchDialog(context, null);
	}
	
	/**
	 * 弹出一个不可编辑的对话框
	 * @param context 上下文对象
	 * @param res 消息内容
	 * @return
	 */
	public static ProgressDialog createCannotTouchDialog(Context context,int res){
		return createCannotTouchDialog(context, context.getString(res));
	}
	
	/**
	 * 弹出一个不可编辑的对话框
	 * @param context 上下文对象
	 * @param message 消息内容
	 * @return
	 */
	public static ProgressDialog createCannotTouchDialog(Context context, String message){
		ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        
        if(message != null){
        	progressDialog.setMessage(message);
        }
		
        return progressDialog;
	}
	
	/**
     * 获取一个PopupWindow对象
     * @param context
     * @param view
     * @return
     */
    @SuppressWarnings("deprecation")
    public static PopupWindow createPopupWhindow(Context context, View view){
     // 创建PopupWindow对象
        PopupWindow popWindow = new PopupWindow(view);
        // 需要设置一下此参数，点击外边可消失
        popWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
        // 设置点击窗口外边窗口消失
        popWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popWindow.setFocusable(true);
        
        return popWindow;
    }
	
	/**
	 * 获取一个PopupWindow对象
	 * @param context
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static PopupWindow createPopupWhindow(Context context, View view, int width, int height){
		// 创建PopupWindow对象
		PopupWindow popWindow = new PopupWindow(view, width, height, false);
		// 需要设置一下此参数，点击外边可消失
		popWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		// 设置点击窗口外边窗口消失
		popWindow.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		popWindow.setFocusable(true);
		
		return popWindow;
	}
}
