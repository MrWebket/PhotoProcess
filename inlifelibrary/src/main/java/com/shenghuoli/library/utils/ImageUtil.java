package com.shenghuoli.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shenghuoli.library.utils.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类
 *
 * @author vendor
 */
public class ImageUtil {

    public static final String TAG = "ImageUtil";

    /**
     * png类型
     */
    public static final int PNG = 0;
    /**
     * jpg类型
     */
    public static final int JPG = 1;


    private ImageUtil() {

    }

    /**
     * 对图片进行压缩处理
     *
     * @param f      图片文件
     * @param width  宽度
     * @param height 高度
     * @return 压缩处理过的图片
     */
    public static Bitmap compress(File f, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
        return compress(bitmap, width, height);
    }

    /**
     * 图像大小压缩
     *
     * @param image  图片文件
     * @param width  宽度
     * @param height 高度
     * @return
     */
    public static Bitmap compress(Bitmap image, int width, int height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int width_tmp = newOpts.outWidth, height_tmp = newOpts.outHeight;

        double mWidth = (double) width_tmp / width;
        double mHeight = (double) height_tmp / height;

        double scale = mWidth > mHeight ? mWidth : mHeight;  //获取需要缩放的倍数

        if (scale <= 1) {
            scale = 1;
        }

        newOpts.inSampleSize = (int) Math.ceil(scale);// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return bitmap;
    }

    /**
     * 图片文件大小压缩
     *
     * @param f
     * @param  size
     * @return
     */
    public static Bitmap compressSize(File f, int size) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));

            return compressSize(bitmap, size);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.getMessage());
            System.gc();
        }
        return null;
    }

    /**
     * 图片文件大小压缩
     *
     * @param image
     * @return
     */
    public static Bitmap compressSize(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            System.out.println("test = " + options);
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10

            if (options == 10) {
                break;
            }
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 图片保存到本地
     *
     * @param context
     * @param resId
     * @return 文件存放地址
     * @throws IOException
     */
    public static String saveBitmap(Context context, int resId) {
        return saveBitmap(context, resId, null, true);
    }

    /**
     * 图片保存到本地
     *
     * @param resId
     * @param fileName
     * @param isCover  是否覆盖已存在的文件
     * @return 文件存放地址
     * @throws IOException
     */
    public static String saveBitmap(Context context, int resId, String fileName, Boolean isCover) {
        Bitmap bmp = convertBitMap(context, resId);
        return saveBitmap(context, bmp, PNG, null, fileName, isCover);
    }

    /**
     * 图片保存到本地
     *
     * @param context
     * @param bmp
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Context context, Bitmap bmp) {
        return saveBitmap(context, bmp, PNG, null, null, true);
    }

    /**
     * 图片保存到本地
     *
     * @param context
     * @param bmp
     * @param type    {@link ImageUtil.PNG} {@link ImageUtil.JPG}
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Context context, Bitmap bmp, int type) {
        return saveBitmap(context, bmp, type, null, null, true);
    }

    /**
     * 图片保存到本地
     *
     * @param bmp
     * @param type     {@link ImageUtil.PNG} {@link ImageUtil.JPG}
     * @param dir      文件存放路径
     * @param fileName 文件名
     * @param isCover  是否覆盖已存在的文件
     * @return
     * @throws IOException
     */
    public static String saveBitmap(Context context, Bitmap bmp, int type, String dir, String fileName, Boolean isCover) {
        if (bmp == null) {
            return null;
        }

        String picPath = null;

        if (fileName == null) {
            switch (type) {
                case JPG:
                    fileName = System.currentTimeMillis() + ".jpg";
                    break;
                default:
                    fileName = System.currentTimeMillis() + ".png";
                    break;
            }
        }

        if (dir == null) {
            StringBuffer sb = new StringBuffer();

            if (SDCardUtil.isSDCardAvaiable()) {
                sb.append(Environment.getExternalStorageDirectory().getPath());
                sb.append(File.separator);
            }

            sb.append(context.getPackageName());
            sb.append("/cache");

            dir = sb.toString();
        }

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

    /**
     * 图片圆角处理
     *
     * @param bitmap
     * @param pixels
     * @return
     * @throws OutOfMemoryError
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) throws OutOfMemoryError {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        return output;
    }

    /**
     * 将id图片转换为bitmap资源
     *
     * @param context
     * @param resId
     */
    public static Bitmap convertBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        try {
            // 获取资源图片
            InputStream is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (NotFoundException e) {
            Log.e(TAG, "NotFoundException");
        }

        return null;
    }

    /**
     * 获取指定Activity的截屏，保存到png文件
     *
     * @param context
     * @return
     */
    public static Bitmap shot(Context context) {
        Activity activity = (Activity) context;
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕长和高
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = null;

        if (b1 != null) {
            b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        }

        view.destroyDrawingCache();

        if (b1 != null && !b.isRecycled()) {
            b1.recycle();
            b1 = null;
        }

        return b;
    }

    /**
     * 从asset中读取图片
     *
     * @param fileName
     * @return
     */
    public static Bitmap getImageFromAssetFile(Context context, String fileName) {
        Bitmap image = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            System.err.print(e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

        }

        return image;
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 图片是否为正方形
     *
     * @param context
     * @param imagePath
     * @return
     */
    public static boolean isSquare(Context context, String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight == options.outWidth;
    }

    /**
     * 得到图片宽高比
     *
     * @param context
     * @param filePath
     * @return
     */
    public static float getImageRadio(Context context, String filePath) {
        InputStream inputStream = null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            inputStream = new FileInputStream(new File(filePath));

            BitmapFactory.decodeStream(inputStream, null, options);
            int initWidth = options.outWidth;
            int initHeight = options.outHeight;
            float rate = initHeight > initWidth ? (float) initHeight / (float) initWidth
                    : (float) initWidth / (float) initHeight;
            return rate;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            IOUtil.closeStream(inputStream);
        }

    }

    public static Bitmap decodeBitmapWithOrientationMax(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, true);
    }

    private static Bitmap decodeBitmapWithSize(String pathName, int width, int height,
                                               boolean useBigger) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(pathName, options);

        int decodeWidth = width, decodeHeight = height;
        final int degrees = getImageDegrees(pathName);
        if (degrees == 90 || degrees == 270) {
            decodeWidth = height;
            decodeHeight = width;
        }

        if (useBigger) {
            options.inSampleSize = (int) Math.min(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        } else {
            options.inSampleSize = (int) Math.max(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        }

        options.inJustDecodeBounds = false;
        Bitmap sourceBm = BitmapFactory.decodeFile(pathName, options);
        return imageWithFixedRotation(sourceBm, degrees);
    }

    public static int getImageDegrees(String pathName) {
        int degrees = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(pathName);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degrees;
    }

    public static Bitmap imageWithFixedRotation(Bitmap bm, int degrees) {
        if (bm == null || bm.isRecycled())
            return null;

        if (degrees == 0)
            return bm;

        final Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        if (result != bm)
            bm.recycle();
        return result;

    }
}
