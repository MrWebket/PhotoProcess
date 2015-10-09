package com.shenghuoli.library.utils;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * widget测量工具<br>
 * 在widget为创建时候调用
 * @author vendor
 */
public class MeasureUtil {
	/**
	 * 计算view的宽高
	 * @param child
	 */
	public static void measureView(View child) {
		if(child instanceof ListView){
			measureView((ListView)child);
			return;
		}
		
		ViewGroup.LayoutParams lp = child.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, lp.width);
		int lpHeight = lp.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	/**
	 * 计算高度
	 * @param listView
	 */
	public static void measureView(ListView listView) {  
        //获取ListView对应的Adapter  
	    ListAdapter listAdapter = listView.getAdapter();   
	    if (listAdapter == null) {  
	        // pre-condition  
	        return;  
	    }  
	
	    int totalHeight = 0;  
	    for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目  
	        View listItem = listAdapter.getView(i, null, listView);  
	        listItem.measure(0, 0);  //计算子项View 的宽高  
	        totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度  
	    }
	
	    ViewGroup.LayoutParams params = listView.getLayoutParams();  
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
	    //listView.getDividerHeight()获取子项间分隔符占用的高度  
	    //params.height最后得到整个ListView完整显示需要的高度  
	    listView.setLayoutParams(params);  
	}
}
