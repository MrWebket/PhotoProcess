package com.hope.photoprocess.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hope.photoprocess.R;
import com.hope.photoprocess.app.App;
import com.hope.photoprocess.camera.CameraManager;
import com.hope.photoprocess.widget.ImageViewTouch.ImageViewTouch;
import com.shenghuoli.library.activitys.BaseActivity;
import com.shenghuoli.library.utils.FileUtil;
import com.shenghuoli.library.utils.IOUtil;
import com.shenghuoli.library.utils.ImageUtil;
import com.shenghuoli.library.utils.LogUtil;
import com.shenghuoli.library.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 图片截取
 * <p>
 * Created by Hope on 15/8/21.
 */
public class CropPhotoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = CropPhotoActivity.class.getSimpleName();

    public static final String EXTRA_FILE_PATH = "extra:filePath";
    public static final String EXTEA_IS_DELETE = "extra:isDelete";

    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;

    private String filePath;

    private Bitmap oriBitmap;

    private int initWidth, initHeight;

    private static final int MAX_WRAP_SIZE = 2048;

    private ImageViewTouch mTouchCropImage;

    private ViewGroup drawArea;

    private View wrapImage;

    private boolean isDelete;

    /**
     * 当前图片的宽高比
     */
    private double rate;

    /**
     * 当前旋转角度
     */
    private float mCurrentrRotationDegree = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState, String tag) {
        setContentView(R.layout.crop_photo);
    }

    @Override
    protected void findView() {
        mTouchCropImage = (ImageViewTouch) findViewById(R.id.crop_image);
        drawArea = (ViewGroup) findViewById(R.id.draw_area);
        wrapImage = findViewById(R.id.wrap_image);

        findViewById(R.id.picked).setOnClickListener(this);
        findViewById(R.id.rotation_btn).setOnClickListener(this);
    }


    @Override
    protected void initialize() {

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_FILE_PATH)) {
            finish();
            return;
        }

        filePath = extras.getString(EXTRA_FILE_PATH);
        isDelete = extras.getBoolean(EXTEA_IS_DELETE, false);

        drawArea.getLayoutParams().height = App.getInstance().getScreenWidth();
        InputStream inputStream = null;
        try {
            //得到图片宽高比
            rate = ImageUtil.getImageRadio(this, filePath);

            oriBitmap = ImageUtil.decodeBitmapWithOrientationMax(filePath, App.getInstance().getScreenWidth(), App.getInstance().getScreenHeight());

            initWidth = oriBitmap.getWidth();
            initHeight = oriBitmap.getHeight();

            mTouchCropImage.setImageBitmap(oriBitmap, new Matrix(), (float) rate, 10);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inputStream);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_center:
                wrapImage.setSelected(!wrapImage.isSelected());
                break;
            case R.id.picked: //截取
                new Thread() {
                    public void run() {
                        cropImage();
                    }

                }.start();
                break;
            case R.id.rotation_btn: //旋转
                setTouchImageBitmap(true);
                break;
        }
    }

    private Bitmap rotationBitmap;

    private void setTouchImageBitmap(boolean changeRotationDegree) {
        Matrix m = new Matrix();

        if (changeRotationDegree) {
            mCurrentrRotationDegree = getRotationDegree(mCurrentrRotationDegree);

            mTouchCropImage.setImageBitmap(null);

            if(rotationBitmap != null && !rotationBitmap.isRecycled()) {
                rotationBitmap.recycle();
                rotationBitmap = null;
            }

            rotationBitmap = adjustPhotoRotation(oriBitmap, mCurrentrRotationDegree);

            mTouchCropImage.setImageBitmap(rotationBitmap, m, (float) rate, 10);
        } else {

        }
    }

    private int mCurrentIndex = 1;

   private Bitmap adjustPhotoRotation(Bitmap bm, final float orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            mCurrentIndex = mCurrentIndex == 1 ? 2 : 1;

            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth() - mCurrentIndex, bm.getHeight() - mCurrentIndex, m, false);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    /**
     * 获取旋转角度
     */
    private float getRotationDegree(float currentDegree) {
        float rotationDegree = currentDegree;
        if (rotationDegree == 270) {
            rotationDegree = 0;
        } else {
            rotationDegree += 90;
        }
        return rotationDegree;
    }

    private void cropImage() {
        Bitmap croppedImage;
        if (IN_MEMORY_CROP) {
            croppedImage = inMemoryCrop(mTouchCropImage);
        } else {
            try {
                croppedImage = decodeRegionCrop(mTouchCropImage);
            } catch (IllegalArgumentException e) {
                croppedImage = inMemoryCrop(mTouchCropImage);
            }
        }
        saveImageToFile(croppedImage);
    }

    private void saveImageToFile(Bitmap croppedImage) {
        if (croppedImage != null) {
            try {
                String imagePath = CameraManager.getInstance().saveBitmapToSDCard(this, croppedImage);

                Bundle extras = new Bundle();
                extras.putString(PhotoProcessActivity.EXTRA_FILE_PATH, imagePath);

                startIntent(PhotoProcessActivity.class, extras);

                croppedImage.recycle();

                if (isDelete) {
                    FileUtil.deleteFile(filePath);
                }
                finish();
                ;
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.show(this, "裁剪图片异常，请稍后重试");
            }
        }
    }

    @TargetApi(10)
    private Bitmap decodeRegionCrop(ImageViewTouch cropImage) {

        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = App.getInstance().getScreenWidth();
        float scale = cropImage.getScale() / getImageRadio();
        RectF rectf = cropImage.getBitmapRect();
        int left = -(int) (rectf.left * width / screenWidth / scale);
        int top = -(int) (rectf.top * width / screenWidth / scale);
        int right = left + (int) (width / scale);
        int bottom = top + (int) (width / scale);
        Rect rect = new Rect(left, top, right, bottom);
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Bitmap sourceBitmap = rotationBitmap != null ? rotationBitmap : oriBitmap;

            sourceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            is = new ByteArrayInputStream(baos.toByteArray());
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
        } catch (Throwable e) {

        } finally {
            IOUtil.closeStream(is);
        }
        return croppedImage;
    }

    private float getImageRadio() {
        return Math.max((float) initWidth, (float) initHeight)
                / Math.min((float) initWidth, (float) initHeight);
    }

    private Bitmap inMemoryCrop(ImageViewTouch cropImage) {
        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = App.getInstance().getScreenWidth();
        System.gc();
        Bitmap croppedImage = null;

        try {
            croppedImage = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            float scale = cropImage.getScale();
            float rotation = cropImage.getRotation();

            RectF srcRect = cropImage.getBitmapRect();
            Matrix matrix = new Matrix();

            matrix.postRotate(rotation);
            matrix.postScale(scale / getImageRadio(), scale / getImageRadio());
            matrix.postTranslate(srcRect.left * width / screenWidth, srcRect.top * width
                    / screenWidth);
            //matrix.mapRect(srcRect);
            canvas.drawBitmap(oriBitmap, matrix, null);
        } catch (OutOfMemoryError e) {
            LogUtil.error(this.getClass(), "截图 OOM");
            System.gc();
        }
        return croppedImage;
    }

    @Override
    protected void onDestroy() {
        if (oriBitmap != null && !oriBitmap.isRecycled()) {
            oriBitmap.recycle();
        }
        if(rotationBitmap != null && !rotationBitmap.isRecycled()) {
            rotationBitmap.recycle();
        }
        super.onDestroy();
    }
}
