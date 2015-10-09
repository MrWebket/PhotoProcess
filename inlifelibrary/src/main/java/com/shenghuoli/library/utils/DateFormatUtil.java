
package com.shenghuoli.library.utils;

import org.apache.http.impl.cookie.DateParseException;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 * 类描述：时间格式化和泛解析工具类
 * 
 * @author dbzhuang
 */
public class DateFormatUtil {
    private DateFormatUtil() {
    };

    // 设置SimpleDateFormat的参照时区，很重要
    // 默认为北京时间对应的东八区
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT+8");

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() {

        @Override
        protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
            return new SoftReference<Map<String, SimpleDateFormat>>(
                    new HashMap<String, SimpleDateFormat>());
        }
    };

    private static SimpleDateFormat formatFor(String pattern) {
        SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
        Map<String, SimpleDateFormat> formats = ref.get();
        if (formats == null) {
            formats = new HashMap<String, SimpleDateFormat>();
            THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
        }

        SimpleDateFormat format = formats.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern, Locale.CHINA);
            format.setTimeZone(GMT);
            formats.put(pattern, format);
        }
        return format;
    }

    /**
     * 时间格式化
     * 
     * @param date：要格式化的时间
     * @param pattern：要格式化的类型
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        SimpleDateFormat formatter = formatFor(pattern);
        return formatter.format(date);
    }

    private static Date parseDate(String dateValue, String dateFormat, Date startDate)
            throws DateParseException {

        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormat == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }

        SimpleDateFormat dateParser = formatFor(dateFormat);
        dateParser.set2DigitYearStart(startDate);
        try {
            return dateParser.parse(dateValue);
        } catch (ParseException pe) {
        }
        throw new DateParseException("Unable to parse the date " + dateValue);
    }

    /**
     * 将格式化过的时间解析为date时间
     * 
     * @param dateValue：要解析的已经格式化过的时间
     * @param dateFormat：时间的格式化类型
     * @return 传入参数不正确或者解析出错时，返回空
     */
    public static Date parseDate(final String dateValue, String dateFormat) {
        try {
            return parseDate(dateValue, dateFormat, null);
        } catch (DateParseException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 返回两个时间相差的天数
     * @param sourceDate ：要比较的时间
     * @param targetDate：目标时间
     *            <p>
     *            注意sourceDate必须比targetDate小
     */
    public static int compareDate(Date sourceDate, Date targetDate) {
        int n = 0;
        Calendar sourceCalendar = Calendar.getInstance();
        Calendar targetCalendar = Calendar.getInstance();
        sourceCalendar.setTime(sourceDate);
        targetCalendar.setTime(targetDate);

        while (!sourceCalendar.after(targetCalendar)) { // 循环对比，直到相等，n 就是所要的结果
            // list.add(df.format(c1.getTime())); // 这里可以把间隔的日期存到数组中 打印出来
            n++;
            sourceCalendar.add(Calendar.DATE, 1); // 比较天数，日期+1
        }
        return n - 1;
    }
    /**
     * 获取距离当前时间多少天后的时间
     * @param intervalDay: 距离的天数
     * @param pattern:要格式化的时间
     * @return
     */
    public static String getIntervalDate(int intervalDay,String pattern){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, intervalDay);
        return formatDate(calendar.getTime(), pattern);
    }
}
