package com.shenghuoli.library.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shenghuoli.library.R;

/**
 * 自定义Toast 
 * 
 * @author sks
 *
 */
public class CustomToast extends Toast {

	public CustomToast(Context context) {
		super(context);
	}

	public static Toast makeText(Context context, CharSequence text, int duration) {
		Toast result = new Toast(context);

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.cusotem_toast, null);
		TextView tv = (TextView) v.findViewById(R.id.message);
		tv.setText(text);

		result.setView(v);
		result.setDuration(duration);

		return result;
	}
	
	public static Toast makeText(Context context, int resId, int duration) {
		Toast result = new Toast(context);

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.cusotem_toast, null);
		TextView tv = (TextView) v.findViewById(R.id.message);
		tv.setText(resId);

		result.setView(v);
		result.setDuration(duration);

		return result;
	}
}
