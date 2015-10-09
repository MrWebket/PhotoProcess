package com.hope.photoprocess.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hope.photoprocess.R;
import com.hope.photoprocess.adapter.PhotoProcessAdjustmentAdapter;
import com.hope.photoprocess.widget.HorizontalListView;

/**
 * 图片处理调整Fragment
 * 
 * @author hope
 */
public class PhotoProcessAdjustmentFragment extends PhotoProcessBaseFragment implements AdapterView.OnItemClickListener {

	/**
	 * 亮度
	 */
	public static final int TAB_BRIGHTNESS = 0;

	/**
	 * 暗角
	 */
	public static final int TAB_VIGNETTE = 2;

	private HorizontalListView mHorizontalListView;

	private int mCurrentTab = 0;

	@Override
	protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, String tag) {
		return inflater.inflate(R.layout.photo_process_adjustment_fragment, container, false);
	}
	
	@Override
	protected void findView() {
		mHorizontalListView = (HorizontalListView) findViewById(R.id.listview);

		mHorizontalListView.setAdapter(new PhotoProcessAdjustmentAdapter(getActivity()));

		mHorizontalListView.setOnItemClickListener(this);
	}

	@Override
	protected void initialize() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
				mCurrentTab = TAB_BRIGHTNESS;
				if(mDelegate != null) {
					mDelegate.onPhotoProcessCallback(this, mCurrentTab);
				}
				break;
			case 1:
				break;
			case 2:
				mCurrentTab = TAB_VIGNETTE;

				if(mDelegate != null) {
					mDelegate.onPhotoProcessCallback(this, mCurrentTab);
				}
				break;
		}
	}
}
