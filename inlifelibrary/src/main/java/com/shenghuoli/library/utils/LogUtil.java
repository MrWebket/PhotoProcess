
package com.shenghuoli.library.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 能打印出类名和行号的日志工具类
 * 
 * @author dbzhuang
 */
public class LogUtil {
    
    /**
     * 是否打印日志，true表示打印日志，false表示不打印。
     */
    private static boolean isEnable = true;
    /**
     * 日志的tag
     */
    private static String sLogTag = "MyLogutil";

    /** 消息所属和消息内容的分隔符 */
    private static final String MSGSPLIT = ":";

    private LogUtil() {

    }

    /**
     * 设置是否打开日志
     * 
     * @param isDebug
     */
    public static void init(boolean isDebug) {
        init(isDebug, null);
    }

    /**
     * 设置是否打开日志和日志tag
     * 
     * @param isDebug
     * @param defTag
     */
    public static void init(boolean isDebug, String defTag) {
        isEnable = isDebug;
        if (!TextUtils.isEmpty(defTag)) {
            sLogTag = defTag;
        }
    }

    /**
     * 根据日志级别，输出日志。
     * <p>
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param ste 堆栈信息。
     *            <p>
     *            如果不需要输出源代码信息，则只需将静态成员属性 showLocSrc设为false即可。
     */
    private static void log(String className, int level, String message, StackTraceElement[] ste) {
        if (ste != null && getStackMsg(className, ste) != null) {
            // 加入源代码定位
            message = getStackMsg(className, ste) + MSGSPLIT + message;
        }
        Log.println(level, sLogTag, message);
    }

    /**
     * 根据堆栈信息得到源代码行信息
     * <p>
     * 原理：本工具类的堆栈下一行即为源代码的最原始堆栈。
     * 
     * @param className 调用类的名称
     * @param ste 堆栈信息
     * @return 调用输出日志的代码所在的类.方法.代码行的相关信息
     *         <p>
     *         如：com.MyClass 类里的 fun()方法调用了Logs.debug("test");
     *         <p>
     *         则堆栈信息为: com.MyClass.fun(MyClass.java 代码行号)
     */
    private static String getStackMsg(String className, StackTraceElement[] ste) {
        if (ste == null) {
            return null;
        }
        int length = ste.length;
        for (int i = 0; i < length; i++) {
            StackTraceElement s = ste[i];
            // 定位本类的堆栈
            if (className.equals(s.getClassName())) {
                return s == null ? "" : s.toString();
            }
        }
        return null;
    }

    /**
     * 输出info信息
     * 
     * @param message
     */
    public static void info(Class<?> cls, String message) {
        // 如果禁止日志，则返回。
        if (!isEnable) {
            return;
        }
        printLog(cls, Log.INFO, message);
    }

    /**
     * 输出debug信息
     * 
     * @param cls ：调用类的名称
     * @param message：日志信息
     */
    public static void debug(Class<?> cls, String message) {
        // 如果禁止日志，则返回。
        if (!isEnable) {
            return;
        }
        printLog(cls, Log.DEBUG, message);
    }

    /**
     * 输出warn信息
     * 
     * @param cls ：调用类的名称
     * @param message：日志信息
     */
    public static void warn(Class<?> cls, String message) {
        // 如果禁止日志，则返回。
        if (!isEnable) {
            return;
        }
        printLog(cls, Log.WARN, message);
    }

    /**
     * 输出error信息
     * 
     * @param cls ：调用类的名称
     * @param message：日志信息
     */
    public static void error(Class<?> cls, String message) {
        // 如果禁止日志，则返回。
        if (!isEnable) {
            return;
        }
        printLog(cls, Log.ERROR, message);
    };

    private static void printLog(Class<?> cls, int level, String message) {
        log(cls.getName(), level, message, Thread.currentThread().getStackTrace());
    }
}
