package com.hope.photoprocess.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hope.photoprocess.R;
import com.hope.photoprocess.adapter.ChoosePhotoAdapter;
import com.hope.photoprocess.util.AlbumsScanCommon;
import com.shenghuoli.library.activitys.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 相册Activity
 *
 * Created by Hope on 15/8/20.
 */
public class ChoosePhotoActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private GridView gridView;

    private ChoosePhotoAdapter mChoosePhotoAdapter;

    private List<String> mDataSource = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState, String tag) {
        setContentView(R.layout.choose_photo);
    }

    @Override
    protected void findView() {
        gridView = (GridView) findViewById(R.id.gridview);

        mChoosePhotoAdapter = new ChoosePhotoAdapter(this);

        gridView.setAdapter(mChoosePhotoAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void initialize() {
        AlbumsScanCommon common = AlbumsScanCommon.getInstance(this);

        common.setBbsAlbumsScreenOkResultListener(new AlbumsScanCommon.BbsAlbumsScreenOkResultListener() {
            @Override
            public void onScreenOnResult(List<String> mAllList, Map<String, ArrayList<String>> group) {
                mDataSource.clear();
                mDataSource.addAll(mAllList);

                mChoosePhotoAdapter.setDataSource(mDataSource);
            }
        });

        common.startScanImages();;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filePath = (String)parent.getItemAtPosition(position);
        if(!TextUtils.isEmpty(filePath)) {
            Bundle extras = new Bundle();
            extras.putString(CropPhotoActivity.EXTRA_FILE_PATH, filePath);

            startIntent(CropPhotoActivity.class, extras);
        }
    }
}
