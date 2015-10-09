package com.hope.photoprocess.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.shenghuoli.library.utils.FileUtil;
import com.shenghuoli.library.utils.SDCardUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;


public class GPUImageCache implements Handler.Callback{
	
	private static final String TAG = GPUImageCache.class.getSimpleName();
	/** png类型 */
	public static final int PNG = 0;
	/** jpg类型 */
	public static final int JPG = 1;
	
	private static final String FOLDER_NAME = "gpu";

	private static final int HANDLER_SEND_CODE = 1;

	private static Map<String, SoftReference<Bitmap>> imageCache;

	private HandlerThread handlerThread;

	private Handler handler; 
	
	private Context mContext;
	
	private int mSaveType = PNG;
	
	private static GPUImageCache mGPUImageCache;
	
	private String mFolderName;
	
	private GPUImageCache(Context context) {
		this.mContext = context;
		
		imageCache = new HashMap<String, SoftReference<Bitmap>>();

		handlerThread = new HandlerThread("rishai.com");
		handlerThread.start(); // 创建HandlerThread后一定要记得start()

		Looper looper = handlerThread.getLooper();
		handler = new Handler(looper);
	}
	
	public static GPUImageCache getInstance(Context mContext) {
		if(mGPUImageCache == null) {
			mGPUImageCache = new GPUImageCache(mContext);
		}
		return mGPUImageCache;
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(this);

	private String getPackageName() {
		return mContext.getPackageName();
	}
	
	private String getFolderNmae() {
		if(TextUtils.isEmpty(mFolderName)) {
			mFolderName = Environment.getExternalStorageDirectory().getPath() + File.separator + getPackageName() + File.separator + FOLDER_NAME + File.separator;
		}
		return mFolderName;
	}

	@Override
	public boolean handleMessage(Message msg) {
		Response model = (Response) msg.obj;
		if (model.listener != null) {
			model.listener.onLoad(model.bitmap);
		}
		return false;
	}

	private class Response {
		public Bitmap bitmap;
		public String key;
		public OnGPULoaderListener listener;
		public GPUImageFilter mGPUImageFilter;
	}


	public void getBitmap(String key, Bitmap bitmap, GPUImageFilter mGPUImageFilter, OnGPULoaderListener listener) {
		Response mResponse = new Response();
		mResponse.key = key;
		mResponse.bitmap = bitmap;
		mResponse.listener = listener;
		mResponse.mGPUImageFilter = mGPUImageFilter;

		if (getBitmapForSoftref(key) != null) {
			mResponse.bitmap = getBitmapForSoftref(key);

			Message msg = Message.obtain();
			msg.what = HANDLER_SEND_CODE;
			msg.obj = mResponse;

			mHandler.sendMessage(msg);
			return;
		}
		
		handler.post(new PostRunnable(mResponse));
	}

	private class PostRunnable implements Runnable {

		private Response model;

		public PostRunnable(Response model) {
			this.model = model;
		}

		@Override
		public void run() {
			//from File
			try {
				File file = new File(getFolderNmae() + File.separator + String.valueOf(model.key.hashCode()));
				if(file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
					model.bitmap = bitmap;
					return;
				}
				
				GPUImage gpuImage = new GPUImage(mContext);
				gpuImage.setImage(model.bitmap);  
				gpuImage.setFilter(model.mGPUImageFilter);  
				Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();  
				model.bitmap = bitmap;
				
				if(SDCardUtil.isSDCardAvaiable()) {
					saveBitmap(mContext, bitmap, mSaveType, getFolderNmae(), String.valueOf(model.key.hashCode()), false);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				imageCache.put(model.key, new SoftReference<Bitmap>(model.bitmap));
				
				Message msg = Message.obtain();
				msg.what = HANDLER_SEND_CODE;
				msg.obj = model;
				
				mHandler.sendMessage(msg);
			}

		}
	}

	/**
	 * 从内存中获取Bitmap
	 * 
	 * @param position
	 * @return
	 */
	private Bitmap getBitmapForSoftref(String position) {
		SoftReference<Bitmap> bitmapcache_ = imageCache.get(position);
		// 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
		if (bitmapcache_ != null) {
			Bitmap bitmap_ = bitmapcache_.get();
			return bitmap_;
		}
		return null;
	}

	public void clear() {
		if (handlerThread != null) {
			handlerThread.quit();
		}
		if(imageCache != null) {
			
			Iterator<Entry<String, SoftReference<Bitmap>>> iterator = imageCache.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<String, SoftReference<Bitmap>> entry = iterator.next();
				
				SoftReference<Bitmap> bitmapcache_ = entry.getValue();
				if (bitmapcache_ != null) {
					Bitmap bitmap_ = bitmapcache_.get();
					
					if(bitmap_ != null) {
						bitmap_.recycle();
					}
				}
			}
			imageCache.clear();
		}
		FileUtil.deleteDirectiory(getFolderNmae());
		mGPUImageCache = null;
	}
	
	private String saveBitmap(Context context, Bitmap bmp, int type, String dir, String fileName, Boolean isCover) {
		if (bmp == null){
			return null;
		}
		
		String picPath = null;
		
		picPath = dir + "/" + fileName;
		
		// 判断文件夹是否存在
		File picFile = new File(dir);
		if (!picFile.exists()) {
			picFile.mkdir();
		}

		File file = new File(picPath);
		if (file.exists()) {
			if (isCover) {
				file.delete();
			} else {
				return picPath;
			}
		}

		try {
            file.createNewFile();
        } catch (IOException e1) {
            return null;
        }

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			switch (type) {
                case JPG:
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    break;
                default:
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    break;
            }
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

		try {
			fOut.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		try {
			fOut.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return picPath;
	}

	public void setSaveType(int type) {
		this.mSaveType = type;
	}
	
	public interface OnGPULoaderListener {
		
		public void onLoad(Bitmap bitmap);
	}
}
