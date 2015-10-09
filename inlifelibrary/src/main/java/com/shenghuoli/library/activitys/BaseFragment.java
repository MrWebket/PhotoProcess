package com.shenghuoli.library.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shenghuoli.library.R;
import com.shenghuoli.library.constants.BaseConstants;
import com.shenghuoli.library.widget.GifView;



/**
 * 基于FragmentActivity的扩展
 * <pre>
 * 格式化开发使用 强制实现
 * findView();
 * initialize();
 * </pre>
 * @author Hope
 *
 */
public abstract class BaseFragment extends Fragment {
    
    private static final String BASE_TAG = "BaseFragment";
    
    private Intent mIntent = null;
    
    /** 加载中的圈圈 */
    private ProgressBar mProgressBar;
    
    /** 加载中的图片 */
    private ImageView mProgressImage;
    
    /** 加载中的图片 */
    private GifView mProgressGifImage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(inflater, container, savedInstanceState, BASE_TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mProgressBar = (ProgressBar)findViewById(R.id.base_progress_bar);
        mProgressImage = (ImageView)findViewById(R.id.base_progress_iv);
        mProgressGifImage = (GifView)findViewById(R.id.base_progress_gif_iv);
        
        this.registerReceiver();
        this.findView();
        this.initialize();
    }
    
    /**
     * 程序执行入口 调用完Activity.onCreateView之后马上调用
     * @param inflater
     * @param savedInstanceState
     * @param tag
     */
    protected abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, String tag);
    
    /**
     * 监听控件 调用完onActivityCreated之后马上调用
     */
    protected abstract void findView();
    
    /**
     * 初始化设定 调用完跟随onActivityCreated之后调用 <br />
     * Fragment与activity不同 Fragment的生命周期决定了view的创建 <br />
     * 所以initialize()需要防止重复调用引起的问题
     */
    protected abstract void initialize();
    
    private void registerReceiver(){
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            // 注册拿到加载状态广播接收器
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(info.packageName + BaseConstants.BROADCASE_ADDRESS));
        } catch (Exception e) {

            Log.e(BASE_TAG, e.getMessage());
        }
    }
    
    //注册广播 ,用于接收耗时任务的处理进度
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

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
                    default:
                        Integer i = bundle.getInt(BaseConstants.BROADCASE_INTENT);
                        if(i != null){
                            BaseFragment.this.onReceiveBroadcast(i);
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
    protected void showLoadingProgressDialog(){
        showLoadingProgressDialog(mProgressBar);
        showLoadingProgressDialog(mProgressImage);
        showLoadingProgressDialog(mProgressGifImage);
    }
    
    /**
     * 显示一个加载中的效果
     * @param progressBar
     */
    protected void showLoadingProgressDialog(ProgressBar progressBar){
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 显示一个加载中的效果
     * @param progressImage
     */
    protected void showLoadingProgressDialog(ImageView progressImage){
        BaseUtils.showProgressAnimation(getActivity(), progressImage);
    }
    
    /**
     * 显示一个加载中的效果
     * @param progressImage
     */
    protected void showLoadingProgressDialog(GifView progressImage){
        if(progressImage != null){
            progressImage.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 隐藏一个加载中的效果
     */
    protected void dismissProgressDialog(){
        dismissProgressDialog(mProgressBar);
        dismissProgressDialog(mProgressImage);
        dismissProgressDialog(mProgressGifImage);
    }
    
    /**
     * 隐藏一个加载中的效果
     * @param progressBar
     */
    protected void dismissProgressDialog(ProgressBar progressBar){
        if(progressBar != null){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 隐藏一个加载中的效果
     * @param progressImage
     */
    protected void dismissProgressDialog(ImageView progressImage){
        BaseUtils.dismissProgressAnimation(progressImage);
    }
    
    /**
     * 隐藏一个加载中的效果
     * @param progressImage
     */
    protected void dismissProgressDialog(GifView progressImage){
        if(progressImage != null){
            progressImage.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * 退出的请求类</br>
     * 广播形式实现全部activity的退出操作
     */
    protected void requestExit(){
        BaseUtils.requestExit(getActivity());
    }
    
    /**
     * 发送一个广播
     * @param value
     */
    protected void sendBroadcast(int value) {
        BaseUtils.sendBroadcast(getActivity(), value);
    }

    /**
     * 接收其他类型的广播
     * @param intent
     */
    protected void onReceiveBroadcast(int intent){
        
    }
    
    protected final View findViewById(int id){
        return getView().findViewById(id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if(broadcastReceiver != null){
            try {
                getActivity().unregisterReceiver(broadcastReceiver);
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
        this.startIntent(getActivity(), cls);
    }

    /**
     * 启动页面跳转
     * 
     * @param cls
     */
    public void startIntent(Class<?> cls, Bundle bundle) {
        this.startIntent(getActivity(), cls, bundle);
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

        if (bundle != null){
            mIntent.putExtras(bundle);
        }
        
        context.startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
    }
    
    /**
     * 启动页面跳转 附带返回
     * 
     * @param cls
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, int requestCode) {
        mIntent = new Intent(getActivity(), cls);
        startActivityForResult(mIntent, requestCode);
        getActivity().overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
    }
    
    /**
     * 启动页面跳转 附带返回
     * 
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        mIntent = new Intent(getActivity(), cls);
        mIntent.putExtras(bundle);
        startActivityForResult(mIntent, requestCode);
        getActivity().overridePendingTransition(R.anim.act_enter_anim, R.anim.act_out_anim);
    }

    /**
     * 控件自封装实现关闭页面的方法<br/>
     * 实现原理及步骤:<br/>
     * 布局文件添加android:onclick="xxx"
     * 
     * @param v
     */
    public void actionFinish(View v) {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.act_out_anim, 0);
    }
    
}
