
package com.shenghuoli.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配的内容是ArrayList的基本适配器，
 * <p>
 * 子类只需要做具体的<strong>
 * {@link BaseAdapter#getView(int, android.view.View, android.view.ViewGroup)}
 * </strong>操作 和调用{@link #setDataSource(ArrayList)}设置数据源即可
 */
public abstract class BaseAbsAdapter<T> extends BaseAdapter {
    protected ArrayList<T> mDataSource = new ArrayList<T>();
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseAbsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(int position) {
        return mDataSource.size() <= position ? null : mDataSource.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    /**
     * 获取字符串
     * 
     * @param resId
     * @return
     */
    protected String getString(int resId) {
		return mContext.getResources().getString(resId);
	}
    
    /**
     * 获取数据源
     * @return
     */
    public List<T> getDataSource() {
        return mDataSource;
    }

    /**
     * 设置适配器的数据
     * 
     * @param dataList
     */
    public void setDataSource(List<T> dataList) {
        mDataSource.clear();
        
        if (dataList != null) {
            mDataSource.addAll(dataList);
        }
        
        notifyDataSetChanged();
    }
}
