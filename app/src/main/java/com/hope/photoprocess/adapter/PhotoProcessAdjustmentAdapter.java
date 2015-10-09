package com.hope.photoprocess.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hope.photoprocess.R;
import com.shenghuoli.library.adapter.BaseAbsAdapter;

import java.util.Arrays;


/**
 * 图片处理--调整
 *
 * @author  Hope
 */
public class PhotoProcessAdjustmentAdapter extends BaseAbsAdapter<Integer> {

    private static Integer[] IMAGE_RESOURCE = new Integer[]{R.mipmap.ic_launcher,R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    private static Integer[] TEXT_RESOURCE = new Integer[]{R.string.brightness,R.string.move_center,R.string.vignette};

    public PhotoProcessAdjustmentAdapter(Context context) {
        super(context);

        setDataSource(Arrays.asList(IMAGE_RESOURCE));
    }

    private class ViewHolder {
        private ImageView mImage;
        private TextView mFilterNameTv;
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

        holder.mImage.setBackgroundResource(IMAGE_RESOURCE[position]);
        holder.mFilterNameTv.setText(TEXT_RESOURCE[position]);

        final ImageView imageview = holder.mImage;

        return convertView;
    }
}
