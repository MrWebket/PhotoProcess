
package com.shenghuoli.library.utils;

import android.Manifest;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * <p>
 * 类描述：SD卡操作工具类，需要权限 {@link Manifest.permission #WRITE_EXTERNAL_STORAGE}
 * 
 * @author dbzhuang
 */
public class SDCardUtil {
    // SD卡的最小剩余容量大小1MB
    private final static long DEFAULT_LIMIT_SIZE = 1;

    /**
     * 判断SD卡是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isSDCardAvaiable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (getSDFreeSize() > DEFAULT_LIMIT_SIZE) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取SDCard的剩余大小
     * 
     * @param context
     * @return 多少MB
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 获取SD卡的总容量
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDAllSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }
}
