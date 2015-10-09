
package com.shenghuoli.library.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类描述：常用方法工具类
 * <p>
 * 作 者：奔跑的猪
 */
public class CommonUtil {
    
    private CommonUtil() {

    }
    
    /**
     * 是否包含中文
     * 
     * @param sequence
     * @return
     */
    public static boolean isContainChinese(String sequence) {
        final String format = "[\\u4E00-\\u9FA5\\uF900-\\uFA2D]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(sequence);
        return matcher.find();
    }

    /**
     * 判断邮箱格式
     */
    public static boolean isEmail(String str) {
        String check = "\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证是否是手机号码
     * 
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        String NUM = "+86";
        boolean flag = false;
        if (TextUtils.isEmpty(str)) {
            return flag;
        } else {
            if (str.indexOf(NUM) > -1) {
                str = str.substring(NUM.length(), str.length());
            }
            if (str.charAt(0) == '0') {
                str = str.substring(1, str.length());
            }
            String rex = "^1\\d{10}$";
            str = removeBlanks(str);
            if (str.matches(rex)) {
                flag = true;
            }
            return flag;
        }
    }

    /**
     * 删除字符串中的空白符
     * 
     * @param content
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }

    /**
     * 18位或者15位身份证验证 18位的最后一位可以是字母x
     * 
     * @param text
     * @return
     */

    public static boolean personIdValidation(String text) {
        boolean flag = false;
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        flag = text.matches(regx) || text.matches(reg1) || text.matches(regex);
        return flag;
    }
    
    public static boolean isAuthenticodeLegal(String yzm) {
        if (null == yzm || "".equals(yzm)) {
            return false;
        }

        return yzm.matches("\\d+");
    }
}
