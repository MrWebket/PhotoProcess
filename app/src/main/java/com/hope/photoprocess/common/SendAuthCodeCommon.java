package com.hope.photoprocess.common;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class SendAuthCodeCommon implements Handler.Callback{

	Handler handler = new Handler(this);

	private OnTimingChangeListener listener;

	private static final int HANDLER_CODE_TIMING = 1;

	/** 最大等待时间 */
	private long maxTryAgainTime = 60000;

	/** 倒计时间隔 */
	private long countDownInterval = 1000;

	private long milliseconds = maxTryAgainTime; // 剩余秒数

	private boolean isTryAgain = true; // 是否能够重新发送
	private CountDownTimer mCountDownTimer;

	public SendAuthCodeCommon(OnTimingChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取是否计时完毕
	 * 
	 * @return
	 */
	public boolean isTryAgain() {
		return isTryAgain;
	}

	public long getMilliSeconds() {
		return milliseconds;
	}
	
	/**
	 * 开始倒计时
	 */
	public void startCountdown() {
		isTryAgain = false;
		handler.removeMessages(HANDLER_CODE_TIMING);
		mCountDownTimer = new CountDownTimer(maxTryAgainTime, countDownInterval) {
			@Override
			public void onFinish() {

				milliseconds = 0;
				isTryAgain = true;
				handler.sendEmptyMessage(HANDLER_CODE_TIMING);
			}

			@Override
			public void onTick(long arg0) {
				milliseconds = arg0;
				handler.sendEmptyMessage(HANDLER_CODE_TIMING);
			}
		};
		
		mCountDownTimer.start();
	}

	/**
	 * 取消定时
	 */
	public void cancel() {
	    isTryAgain = true;
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}
	
	/**
	 * 设置最大等待时间
	 * 
	 * @param time
	 */
	public void setMaxTryAgainTime(long time) {
		maxTryAgainTime = time;
	}

	/**
	 * 设置倒计时间隔
	 * 
	 * @param countDownInterval
	 */
	public void setCountDownInterval(int countDownInterval) {
		this.countDownInterval = countDownInterval;
	}

	@Override
	public boolean handleMessage(Message msg) {
		int what = msg.what;
		switch (what) {
			case HANDLER_CODE_TIMING:
				if (listener != null) {
					listener.onTimingChange(milliseconds);
				}
				break;

			default:
				break;
		}
		return false;
	}

	public interface OnTimingChangeListener {

		public void onTimingChange(long milliseconds);
	}

}
