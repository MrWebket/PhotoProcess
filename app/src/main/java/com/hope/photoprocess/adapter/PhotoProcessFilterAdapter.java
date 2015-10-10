package com.hope.photoprocess.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hope.photoprocess.R;
import com.hope.photoprocess.cache.GPUImageCache;
import com.hope.photoprocess.model.GPUEntity;
import com.shenghuoli.library.adapter.BaseAbsAdapter;
import com.shenghuoli.library.utils.DisplayUtil;


@SuppressLint("InflateParams")
public class PhotoProcessFilterAdapter extends BaseAbsAdapter<GPUEntity> {

    private Bitmap bitmap;

    private String currentTimeMillis;

    public PhotoProcessFilterAdapter(Context context) {
        super(context);
        currentTimeMillis = String.valueOf(System.currentTimeMillis());
    }

    private class ViewHolder {
        private ImageView mImage;
        private TextView mFilterNameTv;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, DisplayUtil.dip2px(mContext, 30),
                DisplayUtil.dip2px(mContext, 60), true);
        if (this.bitmap == bitmap) {
            this.bitmap = Bitmap.createBitmap(bitmap);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.photo_process_fragment_filter_item, null);

            holder.mImage = (ImageView) convertView.findViewById(R.id.gpuimage);
            holder.mFilterNameTv = (TextView) convertView.findViewById(R.id.gpu_name_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ImageView imageview = holder.mImage;

        if (mDataSource.get(position).gpuImageFilter == null) {
            imageview.setImageBitmap(bitmap);
        } else {
            GPUImageCache.getInstance(mContext).getBitmap(position + currentTimeMillis,
                    bitmap, mDataSource.get(position).gpuImageFilter,
                    new GPUImageCache.OnGPULoaderListener() {

                        @Override
                        public void onLoad(Bitmap bitmap) {
                            imageview.setImageBitmap(bitmap);
                        }
                    });
        }

        holder.mFilterNameTv.setText(mDataSource.get(position).mFilterName);

        return convertView;
    }
}
