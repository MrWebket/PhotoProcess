
package com.shenghuoli.library.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    
    protected ArrayList<T> mDataSource = new ArrayList<T>();
    
    protected LayoutInflater mInflater;
    
    protected Context mContext;
    
    public BaseRecyclerAdapter(Context context){
    	
    	mContext = context;
    	
        mInflater = LayoutInflater.from(mContext);
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
    
    public T getItem(int position){
        return mDataSource.get(position);
    }
    
    @Override
    public int getItemCount() {
        return mDataSource == null ? 0 : mDataSource.size();
    }
    
    @Override
    public void onBindViewHolder(VH viewHolder, final int position) {
        //点击事件
        if(mItemClickListener != null){
            if(mOnClickListener == null){
                mOnClickListener = new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        int position = (Integer)v.getTag();
                        mItemClickListener.onItemClickListener(position);
                    }
                };
            }
            
            viewHolder.itemView.setTag(position);
            viewHolder.itemView.setOnClickListener(mOnClickListener);
        }else{
            viewHolder.itemView.setOnClickListener(null);
        }
        
        //长按事件
        if(mItemLongClickListener != null){
            if(mOnLongClickListener == null){
                mOnLongClickListener = new OnLongClickListener() {
                    
                    @Override
                    public boolean onLongClick(View v) {
                        int position = (Integer)v.getTag();
                        return mItemLongClickListener.onItemLongClickListener(position);
                    }
                };
            }
            
            viewHolder.itemView.setTag(position);
            viewHolder.itemView.setOnLongClickListener(mOnLongClickListener);
        }else{
            viewHolder.itemView.setOnLongClickListener(null);
        }
        
        onBindViewHolder(viewHolder, getItem(position), position);
    }
    
    /**
     * @see RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     * @param viewHolder
     * @param t
     * @param position
     */
    public abstract void onBindViewHolder(VH viewHolder, T t, final int position);

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    
    /**
     * 设置Item长按监听
     * @param listener
     */
    public void setOnItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }
    
    public interface ItemClickListener{
        
        public void onItemClickListener(int position);
    }
    
    public interface ItemLongClickListener{
        
        public boolean onItemLongClickListener(int position);
    }

}
