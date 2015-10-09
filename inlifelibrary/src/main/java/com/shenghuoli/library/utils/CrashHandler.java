package com.shenghuoli.library.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/**
 *  <p>类描述：异常捕获处理器
 *  @author dbzhuang
 */
public class CrashHandler implements UncaughtExceptionHandler{
    //每个小时的日志都是记录在同一个文件
    private final static String LOG_FILE_CREATE_TIME_FORMAT = "yyyy-MM-dd_HH";
    private final static String LOG_FILE_SUFFIX = ".log";
    //日志记入的时间
    private final static String LOG_RECORD_TIME_FORMAT ="yyyy-MM-dd HH mm:ss";
    
    private UncaughtExceptionHanlderListener mHanlderListener;
    
    private static CrashHandler sInstance;
    //设置日志所在文件夹路径
    private String mLogDir;
    
    public static CrashHandler getInstance(Context context){
        if (sInstance == null) {
            sInstance = new CrashHandler();
        }
        return sInstance;
    }
    
    private CrashHandler(){
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        hanldeException(ex);
        if (mHanlderListener != null) {
            mHanlderListener.handlerUncaughtException();
        }
    }
    
    /**
     * 设置外部要处理异常发生时操作监听器
     * @param hanlderListener : {@link UncaughtExceptionHanlderListener}
     */
    public void setHanlderListener(UncaughtExceptionHanlderListener hanlderListener) {
        this.mHanlderListener = hanlderListener;
    }
    /**
     * 设置日志所在文件夹路径
     * @param logDirPath
     */
    public void setCrashLogDir(String logDirPath){
        mLogDir = logDirPath;
    }

    //崩溃日志的保存操作
    private void hanldeException(Throwable ex){
        if (ex == null) {
            return ;
        }
        if (SDCardUtil.isSDCardAvaiable() && !TextUtils.isEmpty(mLogDir)) {
            saveCrashInfoToFile(ex);
        }
    }

    //保存错误信息到文件中
    private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String content = info.toString();
        printWriter.close();
        StringBuffer sb = new StringBuffer();
        long time = System.currentTimeMillis();
        sb.append(">>>>>>>>>>>>>> ");
        sb.append(DateFormatUtil.formatDate(new Date(time), LOG_RECORD_TIME_FORMAT));
        sb.append(">>>>>>>>>>>>>> ");
        sb.append("\r\n");
        sb.append(content);
        sb.append("\r\n");
        sb.append("\r\n");
        LogUtil.error(CrashHandler.class,sb.toString());
        FileUtil.writeToFile(mLogDir,generateLogFileName("error",time), sb.toString(), "utf-8");
        return ;
    }
    //生成日志文件名
    private String generateLogFileName(String prefix,long time){
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("_");
        sb.append(DateFormatUtil.formatDate(new Date(time),LOG_FILE_CREATE_TIME_FORMAT));
        sb.append(LOG_FILE_SUFFIX);
        return sb.toString();
    }
    /**
     * 未捕获异常的处理监听器
     */
    public static interface UncaughtExceptionHanlderListener{
        /**
         * 当获取未捕获异常时的处理
         * 一般用于关闭界面和数据库、网络连接等等
         */
        public void handlerUncaughtException();
    }
}
