package com.hope.photoprocess.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hope.photoprocess.R;
import com.hope.photoprocess.widget.HorizontalListView;


/**
 * 图片处理模板Fragment
 * 
 * @author hope
 */
public class PhotoProcessTemplateFragment extends PhotoProcessBaseFragment implements OnClickListener {

	private HorizontalListView mHorizontalListView;

	@Override
	protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, String tag) {

		return inflater.inflate(R.layout.photo_process_fragment, container, false);
	}

	
	@Override
	protected void findView() {
		mHorizontalListView = (HorizontalListView) findViewById(R.id.listview);
	}

	@Override
	protected void initialize() {

	}


	@Override
	public void onClick(View v) {

	}
}
