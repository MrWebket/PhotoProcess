package com.hope.photoprocess.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hope.photoprocess.app.App;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相册扫描工具类
 *
 */
public class AlbumsScanCommon implements Handler.Callback{

	private ArrayList<String> mAllList = new ArrayList<String>();

    private Map<String, ArrayList<String>> mGroup = new HashMap<String, ArrayList<String>>();

	private final static int HANDLER_CODE_SCAN_OK = 1;

	private static AlbumsScanCommon instance;

	private BbsAlbumsScreenOkResultListener mBbsAlbumsScreenOkResultListener;

	private static Context mContext;

	private Handler mHandler = new Handler(this);

	private AlbumsScanCommon() {
	}

	public static AlbumsScanCommon getInstance(Context context) {
		if (instance == null) {
			instance = new AlbumsScanCommon();
		}

		mContext = context;

		return instance;
	}

	public void clear() {
		if (mAllList != null) {
			mAllList.clear();
		}
	}

	private String mFilter;

	public void setFilter(String filter) {
		this.mFilter = filter;
	}

	private String getFilter() {
		if (TextUtils.isEmpty(mFilter)) {
			mFilter = App.getInstance().getPhotoPath();
		}
		return mFilter;
	}

	public AlbumsScanCommon setBbsAlbumsScreenOkResultListener(BbsAlbumsScreenOkResultListener mBbsAlbumsScreenOkResultListener) {
		this.mBbsAlbumsScreenOkResultListener = mBbsAlbumsScreenOkResultListener;

		return instance;
	}

	/**
	 * 扫描本地图片
	 */
	public void startScanImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		mAllList.clear();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					if(!path.startsWith(getFilter())) {
                        // 获取该图片的父路径名
                        String parentName = new File(path).getParentFile().getName();

                        ArrayList<String> chileList = null;

                        // 根据父路径名将图片放入到mGruopMap中
                        if (!mGroup.containsKey(parentName)) {
                            chileList = new ArrayList<String>();
                            chileList.add(path);
                            mGroup.put(parentName, chileList);
                        } else {
                            mGroup.get(parentName).add(path);
                        }

                        mAllList.add(path);
					}
				}

				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(HANDLER_CODE_SCAN_OK);

			}
		}).start();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLER_CODE_SCAN_OK:
				if (mBbsAlbumsScreenOkResultListener != null) {
					mBbsAlbumsScreenOkResultListener.onScreenOnResult(mAllList, mGroup);
				}
				break;
		}
		return false;
	}

	public interface BbsAlbumsScreenOkResultListener {
        /**
         * 系统照片扫描完成的回调接口
         * @param mAllList
         * @param group
         */
		public void onScreenOnResult(List<String> mAllList, Map<String, ArrayList<String>> group);
	}
}
