package com.hope.photoprocess.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hope.photoprocess.R;
import com.shenghuoli.library.adapter.BaseAbsAdapter;
import com.vendor.library.utils.imageloader.core.DisplayImageOptions;
import com.vendor.library.utils.imageloader.core.ImageLoader;
import com.vendor.library.utils.imageloader.core.assist.ImageScaleType;


public class ChoosePhotoAdapter extends BaseAbsAdapter<String> {

    private DisplayImageOptions mDisplayImageOptions;

    public ChoosePhotoAdapter(Context context) {
        super(context);

        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.choose_photo_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = "file://" + mDataSource.get(position);

        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.imageView, mDisplayImageOptions);

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
    }
}
