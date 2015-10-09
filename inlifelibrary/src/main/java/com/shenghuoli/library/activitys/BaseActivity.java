package com.shenghuoli.library.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shenghuoli.library.R;
import com.shenghuoli.library.constants.BaseConstants;
import com.shenghuoli.library.widget.GifView;

/**
 * 基于FragmentActivity的扩展
 * 
 * <pre>
 * 格式化开发使用 强制实现
 * onCreate();
 * findView();
 * initialize();
 * </pre>
 * 
 * @author Hope
 * 
 */
public abstract class BaseActivity extends Activity {

	public static final String BASE_TAG = "BaseActivity";

	private Intent mIntent = null;

	/**
	 * 是否已经注册广播监听
	 */
	private boolean mIsRegisterReceiver = false;

	/** 加载中的圈圈 */
	private ProgressBar mProgressBar;

	/** 加载中的图片 */
	private ImageView mProgressImage;

	/** 加载中的图片 */
	private GifView mProgressGifImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onCreate(savedInstanceState, BASE_TAG);
	}

	/**
	 * 程序执行入口 调用完Activity.onCreate之后马上调用
	 * 
	 * @param savedInstanceState
	 * @param tag
	 */
	protected abstract void onCreate(Bundle savedInstanceState, String tag);

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		this.init();
	}

	/**
	 * 扩展setContentView 实现不初始化加载资源配置
	 * 
	 * @param layoutResID
	 * @param isInit
	 */
	public void setContentView(int layoutResID, boolean isInit) {
		super.setContentView(layoutResID);

		if (isInit) {
			this.init();
		}
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		this.init();
	}

	/**
	 * 扩展setContentView 实现不初始化加载资源配置
	 * 
	 * @param view
	 * @param isInit
	 */
	public void setContentView(View view, boolean isInit) {
		super.setContentView(view);

		if (isInit) {
			this.init();
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);

		this.init();
	}

	private void init() {
		mProgressBar = (ProgressBar) findViewById(R.id.base_progress_bar);
		mProgressImage = (ImageView) findViewById(R.id.base_progress_iv);
		mProgressGifImage = (GifView) findViewById(R.id.base_progress_gif_iv);

		this.registerReceiver();
		this.findView();
		this.initialize();
	}

	/**
	 * 监听控件 调用完setContentView之后马上调用
	 */
	protected abstract void findView();

	/**
	 * 初始化设定 调用完跟随findView之后调用
	 */
	protected abstract void initialize();

	protected void registerReceiver() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			// 注册拿到加载状态广播接收器
			registerReceiver(broadcastReceiver, new IntentFilter(info.packageName + BaseConstants.BROADCASE_ADDRESS));

			mIsRegisterReceiver = true;
		} catch (Exception e) {
			Log.e(BASE_TAG, e.getMessage());
		}
	}

	// 注册广播 ,用于接收耗时任务的处理进度
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

				if (intent.getAction().equals(info.packageName + BaseConstants.BROADCASE_ADDRESS)) {
					Bundle bundle = intent.getExtras();

					switch (bundle.getInt(BaseConstants.BROADCASE_INTENT)) {
					case BaseConstants.BROADCASE_INTENT_HTTP:
						if (bundle.getBoolean(BaseConstants.BROADCASE_TYPE_STATE)) {
							showLoadingProgressDialog();
						} else {
							dismissProgressDialog();
						}
						break;
					case BaseConstants.BROADCASE_INTENT_EXIT:
						finish();
						break;
					default:
						Integer i = bundle.getInt(BaseConstants.BROADCASE_INTENT);
						if (i != null) {
							BaseActivity.this.onReceiveBroadcast(i);
						}
						break;
					}
				}
			} catch (NameNotFoundException e) {
				Log.e(BASE_TAG, e.getMessage());
			}
		}
	};

	/**
	 * 显示一个加载中的效果
	 */
	protected void showLoadingProgressDialog() {
		showLoadingProgressDialog(mProgressBar);
		showLoadingProgressDialog(mProgressImage);
		showLoadingProgressDialog(mProgressGifImage);
	}

	/**
	 * 显示一个加载中的效果
	 * 
	 * @param progressBar
	 */
	protected void showLoadingProgressDialog(ProgressBar progressBar) {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示一个加载中的效果
	 * 
	 * @param progressImage
	 */
	protected void showLoadingProgressDialog(ImageView progressImage) {
		BaseUtils.showProgressAnimation(this, progressImage);
	}

	/**
	 * 显示一个加载中的效果
	 * 
	 * @param progressImage
	 */
	protected void showLoadingProgressDialog(GifView progressImage) {
		if (progressImage != null) {
			progressImage.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏一个加载中的效果
	 *
	 */
	protected void dismissProgressDialog() {
		dismissProgressDialog(mProgressBar);
		dismissProgressDialog(mProgressImage);
		dismissProgressDialog(mProgressGifImage);
	}

	/**
	 * 隐藏一个加载中的效果
	 * 
	 * @param progressBar
	 */
	protected void dismissProgressDialog(ProgressBar progressBar) {
		if (progressBar != null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 隐藏一个加载中的效果
	 * 
	 * @param progressImage
	 */
	protected void dismissProgressDialog(ImageView progressImage) {
		BaseUtils.dismissProgressAnimation(progressImage);
	}

	/**
	 * 隐藏一个加载中的效果
	 * 
	 * @param progressImage
	 */
	protected void dismissProgressDialog(GifView progressImage) {
		if (progressImage != null) {
			progressImage.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 退出的请求类</br> 广播形式实现全部activity的退出操作
	 *
	 */
	protected void requestExit() {
		BaseUtils.requestExit(this);
	}

	/**
	 * 发送一个广播
	 * 
	 * @param value
	 */
	protected void sendBroadcast(int value) {
		BaseUtils.sendBroadcast(this, value);
	}

	/**
	 * 接收其他类型的广播
	 * 
	 * @param intent
	 */
	protected void onReceiveBroadcast(int intent) {

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mIsRegisterReceiver && broadcastReceiver != null) {
			try {
				mIsRegisterReceiver = false;
				this.unregisterReceiver(broadcastReceiver);
			} catch (Exception e) {
				Log.e(BASE_TAG, e.getMessage());
			} finally {
				broadcastReceiver = null;
			}
		}
	}

	/**
	 * 启动页面跳转
	 * 
	 * @param cls
	 */
	public void startIntent(Class<?> cls) {
		this.startIntent(this, cls);
	}

	/**
	 * 启动页面跳转
	 * 
	 * @param cls
	 */
	public void startIntent(Class<?> cls, Bundle bundle) {
		this.startIntent(this, cls, bundle);
	}

	/**
	 * 启动页面跳转
	 * 
	 * @param context
	 * @param cls
	 */
	public void startIntent(Context context, Class<?> cls) {
		this.startIntent(context, cls, null);
	}

	/**
	 * 启动页面跳转
	 * 
	 * @param context
	 * @param cls
	 * @param bundle
	 */
	public void startIntent(Context context, Class<?> cls, Bundle bundle) {
		mIntent = new Intent(context, cls);

		if (bundle != null) {
			mIntent.putExtras(bundle);
		}

		context.startActivity(mIntent);
		overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
	}

	/**
	 * 启动页面跳转 附带返回
	 * 
	 * @param cls
	 * @param requestCode
	 */
	public void startActivityForResult(Class<?> cls, int requestCode) {
		mIntent = new Intent(this, cls);
		startActivityForResult(mIntent, requestCode);
		overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
	}

	/**
	 * 启动页面跳转 附带返回
	 * 
	 * @param cls
	 * @param bundle
	 * @param requestCode
	 */
	public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
		mIntent = new Intent(this, cls);
		mIntent.putExtras(bundle);
		startActivityForResult(mIntent, requestCode);
		overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
	}

	/**
	 * 控件自封装实现关闭页面的方法<br/>
	 * 实现原理及步骤:<br/>
	 * 布局文件添加android:onclick="xxx"
	 * 
	 * @param v
	 */
	public void actionFinish(View v) {
		this.finish();
		overridePendingTransition(R.anim.act_finish_enter_anim, R.anim.act_finish_out_anim);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			actionFinish(null);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}