package com.hope.photoprocess.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.shenghuoli.android.R;
import com.shenghuoli.android.adapter.PhotoProcessFilterAdapter;
import com.shenghuoli.android.app.App;
import com.shenghuoli.android.camera.GPUImageCache;
import com.shenghuoli.android.camera.PhotoProcessBaseFragment;
import com.shenghuoli.android.camera.entity.GPUEntity;
import com.shenghuoli.android.widget.HorizontalListView;

import java.util.Arrays;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.IF1977Filter;
import jp.co.cyberagent.android.gpuimage.filter.IFLordKelvinFilter;
import jp.co.cyberagent.android.gpuimage.filter.IFSutroFilter;

/**
 * 图片处理滤镜Fragment
 * 
 * @author hope
 */
public class PhotoProcessFilterFragment extends PhotoProcessBaseFragment implements OnClickListener, AdapterView.OnItemClickListener {

    private PhotoProcessFilterAdapter mGPUListAdapter;

    private Bitmap bitmap;

    private HorizontalListView mRecyclerView;

    private static final GPUEntity[] GPU_BEANS = { new GPUEntity(), new GPUEntity(), new GPUEntity(), new GPUEntity(), new GPUEntity(),
            new GPUEntity(), new GPUEntity(), new GPUEntity()};

    static {
        for (int i = 0; i < GPU_BEANS.length; i++) {
            GPUEntity bean = GPU_BEANS[i];
            GPUImageFilter filter = null;
            switch (i) {
                case 0:
                    bean.mFilterName = "原图";
                    break;
                case 1:
                    filter = new GPUImageSepiaFilter();
                    bean.mFilterName = "立春";
                    break;
                case 2:
                    filter = new GPUImageGrayscaleFilter();
                    bean.mFilterName = "惊蛰";
                    break;
                case 3:
                    filter = new GPUImageSharpenFilter();
                    bean.mFilterName = "小满";
                    break;
                case 4:
                    filter = new IFSutroFilter(App.getInstance());
                    bean.mFilterName = "夏至";
                    break;
                case 5:
                    filter = new GPUImageGammaFilter();
                    bean.mFilterName = "立秋";
                    break;
                case 6:
                    filter = new IFLordKelvinFilter(App.getInstance());
                    bean.mFilterName = "小雪 ";
                    break;
                case 7:
                    filter = new IF1977Filter(App.getInstance());
                    bean.mFilterName = "冬至 ";
                    break;
            }
            bean.gpuImageFilter = filter;
        }
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, String tag) {
        return inflater.inflate(R.layout.photo_process_fragment, container, false);
    }

    @Override
    protected void findView() {
        mRecyclerView = (HorizontalListView) findViewById(R.id.listview);
        mRecyclerView.getBackground().setAlpha((int) (255 * 0.9));

        mRecyclerView.setOnItemClickListener(this);

    }

    @Override
    protected void initialize() {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGPUListAdapter = new PhotoProcessFilterAdapter(getActivity());

        if (this.bitmap != null) {
            mGPUListAdapter.setBitmap(this.bitmap);
        }

        mRecyclerView.setAdapter(mGPUListAdapter);

        mGPUListAdapter.setDataSource(Arrays.asList(GPU_BEANS));
    }

    @Override
    public void onStop() {
        super.onStop();
        GPUImageCache.getInstance(getActivity()).clear();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;

        if (mGPUListAdapter != null) {
            mGPUListAdapter.setBitmap(this.bitmap);
        }
    }

    private void callback(int result) {
        if (mDelegate != null) {
            mDelegate.onPhotoProcessCallback(this, result);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback(position);
    }

    /**
     * 用指定序号的滤镜生成图片。
     *
     * @param context
     * @param source
     *            原图
     * @param beanIndex
     *            滤镜的序号
     * @return 如果指定0为原图，则返回null。
     */
    public Bitmap createGPUImageBitmap(Context context, Bitmap source, int beanIndex) {
        GPUImageFilter filter = GPU_BEANS[beanIndex].gpuImageFilter;
        Bitmap bitmap = null;
        if (filter != null) {
            try {
                GPUImage gpuImage = new GPUImage(context);
                gpuImage.setImage(source);
                gpuImage.setFilter(filter);
                bitmap = gpuImage.getBitmapWithFilterApplied();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public void onClick(View v) {

    }
}
