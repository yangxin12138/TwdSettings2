package com.twd.setting.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private static final String[] CHINESE_ZODIAC;
    private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = new ThreadLocal();
    private static final String[] ZODIAC = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};
    private static final int[] ZODIAC_FLAGS;

    static {
        CHINESE_ZODIAC = new String[]{"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};
        ZODIAC_FLAGS = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22};
    }

    private TimeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static long date2Millis(Date paramDate) {
        return paramDate.getTime();
    }

    public static String date2String(Date paramDate) {
        return date2String(paramDate, getDefaultFormat());
    }

    public static String date2String(Date paramDate, String paramString) {
        return getDateFormat(paramString).format(paramDate);
    }

    public static String date2String(Date paramDate, DateFormat paramDateFormat) {
        return paramDateFormat.format(paramDate);
    }

    public static String getChineseWeek(long paramLong) {
        return getChineseWeek(new Date(paramLong));
    }

    public static String getChineseWeek(String paramString) {
        return getChineseWeek(string2Date(paramString, getDefaultFormat()));
    }

    public static String getChineseWeek(String paramString, DateFormat paramDateFormat) {
        return getChineseWeek(string2Date(paramString, paramDateFormat));
    }

    public static String getChineseWeek(Date paramDate) {
        return new SimpleDateFormat("E", Locale.CHINA).format(paramDate);
    }

    public static String getChineseZodiac(int paramInt) {
        return CHINESE_ZODIAC[(paramInt % 12)];
    }

    public static String getChineseZodiac(long paramLong) {
        return getChineseZodiac(millis2Date(paramLong));
    }

    public static String getChineseZodiac(String paramString) {
        return getChineseZodiac(string2Date(paramString, getDefaultFormat()));
    }

    public static String getChineseZodiac(String paramString, DateFormat paramDateFormat) {
        return getChineseZodiac(string2Date(paramString, paramDateFormat));
    }

    public static String getChineseZodiac(Date paramDate) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        return CHINESE_ZODIAC[(localCalendar.get(1) % 12)];
    }

    public static Date getDate(long paramLong1, long paramLong2, int paramInt) {
        return millis2Date(paramLong1 + timeSpan2Millis(paramLong2, paramInt));
    }

    public static Date getDate(String paramString, long paramLong, int paramInt) {
        return getDate(paramString, getDefaultFormat(), paramLong, paramInt);
    }

    public static Date getDate(String paramString, DateFormat paramDateFormat, long paramLong, int paramInt) {
        return millis2Date(string2Millis(paramString, paramDateFormat) + timeSpan2Millis(paramLong, paramInt));
    }

    public static Date getDate(Date paramDate, long paramLong, int paramInt) {
        return millis2Date(date2Millis(paramDate) + timeSpan2Millis(paramLong, paramInt));
    }

    public static Date getDateByNow(long paramLong, int paramInt) {
        return getDate(getNowMillis(), paramLong, paramInt);
    }

    private static SimpleDateFormat getDateFormat(String paramString) {
        ThreadLocal localThreadLocal = SDF_THREAD_LOCAL;
        SimpleDateFormat localSimpleDateFormat = (SimpleDateFormat) localThreadLocal.get();
        if (localSimpleDateFormat == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString, Locale.getDefault());
            localThreadLocal.set(simpleDateFormat);
            return simpleDateFormat;
        }
        localSimpleDateFormat.applyPattern(paramString);
        return localSimpleDateFormat;
    }

    private static SimpleDateFormat getDefaultFormat() {
        return getDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String getFitTimeSpan(long paramLong1, long paramLong2, int paramInt) {
        return millis2FitTimeSpan(paramLong1 - paramLong2, paramInt);
    }

    public static String getFitTimeSpan(String paramString1, String paramString2, int paramInt) {
        return millis2FitTimeSpan(string2Millis(paramString1, getDefaultFormat()) - string2Millis(paramString2, getDefaultFormat()), paramInt);
    }

    public static String getFitTimeSpan(String paramString1, String paramString2, DateFormat paramDateFormat, int paramInt) {
        return millis2FitTimeSpan(string2Millis(paramString1, paramDateFormat) - string2Millis(paramString2, paramDateFormat), paramInt);
    }

    public static String getFitTimeSpan(Date paramDate1, Date paramDate2, int paramInt) {
        return millis2FitTimeSpan(date2Millis(paramDate1) - date2Millis(paramDate2), paramInt);
    }

    public static String getFitTimeSpanByNow(long paramLong, int paramInt) {
        return getFitTimeSpan(paramLong, System.currentTimeMillis(), paramInt);
    }

    public static String getFitTimeSpanByNow(String paramString, int paramInt) {
        return getFitTimeSpan(paramString, getNowString(), getDefaultFormat(), paramInt);
    }

    public static String getFitTimeSpanByNow(String paramString, DateFormat paramDateFormat, int paramInt) {
        return getFitTimeSpan(paramString, getNowString(paramDateFormat), paramDateFormat, paramInt);
    }

    public static String getFitTimeSpanByNow(Date paramDate, int paramInt) {
        return getFitTimeSpan(paramDate, getNowDate(), paramInt);
    }

    public static String getFriendlyTimeSpanByNow(long paramLong) {
        long l = System.currentTimeMillis() - paramLong;
        if (l < 0L) {
            return String.format("%tc", new Object[]{Long.valueOf(paramLong)});
        }
        if (l < 1000L) {
            return "刚刚";
        }
        if (l < 60000L) {
            return String.format(Locale.getDefault(), "%d秒前", new Object[]{Long.valueOf(l / 1000L)});
        }
        if (l < 3600000L) {
            return String.format(Locale.getDefault(), "%d分钟前", new Object[]{Long.valueOf(l / 60000L)});
        }
        l = getWeeOfToday();
        if (paramLong >= l) {
            return String.format("今天%tR", new Object[]{Long.valueOf(paramLong)});
        }
        if (paramLong >= l - 86400000L) {
            return String.format("昨天%tR", new Object[]{Long.valueOf(paramLong)});
        }
        return String.format("%tF", new Object[]{Long.valueOf(paramLong)});
    }

    public static String getFriendlyTimeSpanByNow(String paramString) {
        return getFriendlyTimeSpanByNow(paramString, getDefaultFormat());
    }

    public static String getFriendlyTimeSpanByNow(String paramString, DateFormat paramDateFormat) {
        return getFriendlyTimeSpanByNow(string2Millis(paramString, paramDateFormat));
    }

    public static String getFriendlyTimeSpanByNow(Date paramDate) {
        return getFriendlyTimeSpanByNow(paramDate.getTime());
    }

    public static long getMillis(long paramLong1, long paramLong2, int paramInt) {
        return paramLong1 + timeSpan2Millis(paramLong2, paramInt);
    }

    public static long getMillis(String paramString, long paramLong, int paramInt) {
        return getMillis(paramString, getDefaultFormat(), paramLong, paramInt);
    }

    public static long getMillis(String paramString, DateFormat paramDateFormat, long paramLong, int paramInt) {
        return string2Millis(paramString, paramDateFormat) + timeSpan2Millis(paramLong, paramInt);
    }

    public static long getMillis(Date paramDate, long paramLong, int paramInt) {
        return date2Millis(paramDate) + timeSpan2Millis(paramLong, paramInt);
    }

    public static long getMillisByNow(long paramLong, int paramInt) {
        return getMillis(getNowMillis(), paramLong, paramInt);
    }

    public static Date getNowDate() {
        return new Date();
    }

    public static long getNowMillis() {
        return System.currentTimeMillis();
    }

    public static String getNowString() {
        return millis2String(System.currentTimeMillis(), getDefaultFormat());
    }

    public static String getNowString(DateFormat paramDateFormat) {
        return millis2String(System.currentTimeMillis(), paramDateFormat);
    }

    public static String getString(long paramLong1, long paramLong2, int paramInt) {
        return getString(paramLong1, getDefaultFormat(), paramLong2, paramInt);
    }

    public static String getString(long paramLong1, DateFormat paramDateFormat, long paramLong2, int paramInt) {
        return millis2String(paramLong1 + timeSpan2Millis(paramLong2, paramInt), paramDateFormat);
    }

    public static String getString(String paramString, long paramLong, int paramInt) {
        return getString(paramString, getDefaultFormat(), paramLong, paramInt);
    }

    public static String getString(String paramString, DateFormat paramDateFormat, long paramLong, int paramInt) {
        return millis2String(string2Millis(paramString, paramDateFormat) + timeSpan2Millis(paramLong, paramInt), paramDateFormat);
    }

    public static String getString(Date paramDate, long paramLong, int paramInt) {
        return getString(paramDate, getDefaultFormat(), paramLong, paramInt);
    }

    public static String getString(Date paramDate, DateFormat paramDateFormat, long paramLong, int paramInt) {
        return millis2String(date2Millis(paramDate) + timeSpan2Millis(paramLong, paramInt), paramDateFormat);
    }

    public static String getStringByNow(long paramLong, int paramInt) {
        return getStringByNow(paramLong, getDefaultFormat(), paramInt);
    }

    public static String getStringByNow(long paramLong, DateFormat paramDateFormat, int paramInt) {
        return getString(getNowMillis(), paramDateFormat, paramLong, paramInt);
    }

    public static long getTimeSpan(long paramLong1, long paramLong2, int paramInt) {
        return millis2TimeSpan(paramLong1 - paramLong2, paramInt);
    }

    public static long getTimeSpan(String paramString1, String paramString2, int paramInt) {
        return getTimeSpan(paramString1, paramString2, getDefaultFormat(), paramInt);
    }

    public static long getTimeSpan(String paramString1, String paramString2, DateFormat paramDateFormat, int paramInt) {
        return millis2TimeSpan(string2Millis(paramString1, paramDateFormat) - string2Millis(paramString2, paramDateFormat), paramInt);
    }

    public static long getTimeSpan(Date paramDate1, Date paramDate2, int paramInt) {
        return millis2TimeSpan(date2Millis(paramDate1) - date2Millis(paramDate2), paramInt);
    }

    public static long getTimeSpanByNow(long paramLong, int paramInt) {
        return getTimeSpan(paramLong, System.currentTimeMillis(), paramInt);
    }

    public static long getTimeSpanByNow(String paramString, int paramInt) {
        return getTimeSpan(paramString, getNowString(), getDefaultFormat(), paramInt);
    }

    public static long getTimeSpanByNow(String paramString, DateFormat paramDateFormat, int paramInt) {
        return getTimeSpan(paramString, getNowString(paramDateFormat), paramDateFormat, paramInt);
    }

    public static long getTimeSpanByNow(Date paramDate, int paramInt) {
        return getTimeSpan(paramDate, new Date(), paramInt);
    }

    public static String getUSWeek(long paramLong) {
        return getUSWeek(new Date(paramLong));
    }

    public static String getUSWeek(String paramString) {
        return getUSWeek(string2Date(paramString, getDefaultFormat()));
    }

    public static String getUSWeek(String paramString, DateFormat paramDateFormat) {
        return getUSWeek(string2Date(paramString, paramDateFormat));
    }

    public static String getUSWeek(Date paramDate) {
        return new SimpleDateFormat("EEEE", Locale.US).format(paramDate);
    }

    public static int getValueByCalendarField(int paramInt) {
        return Calendar.getInstance().get(paramInt);
    }

    public static int getValueByCalendarField(long paramLong, int paramInt) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(paramLong);
        return localCalendar.get(paramInt);
    }

    public static int getValueByCalendarField(String paramString, int paramInt) {
        return getValueByCalendarField(string2Date(paramString, getDefaultFormat()), paramInt);
    }

    public static int getValueByCalendarField(String paramString, DateFormat paramDateFormat, int paramInt) {
        return getValueByCalendarField(string2Date(paramString, paramDateFormat), paramInt);
    }

    public static int getValueByCalendarField(Date paramDate, int paramInt) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        return localCalendar.get(paramInt);
    }

    private static long getWeeOfToday() {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(11, 0);
        localCalendar.set(13, 0);
        localCalendar.set(12, 0);
        localCalendar.set(14, 0);
        return localCalendar.getTimeInMillis();
    }

    public static String getZodiac(int paramInt1, int paramInt2) {
        String[] arrayOfString = ZODIAC;
        int[] arrayOfInt = ZODIAC_FLAGS;
        int i = paramInt1 - 1;
        if (paramInt2 >= arrayOfInt[i]) {
            paramInt1 = i;
        } else {
            paramInt1 = (paramInt1 + 10) % 12;
        }
        return arrayOfString[paramInt1];
    }

    public static String getZodiac(long paramLong) {
        return getZodiac(millis2Date(paramLong));
    }

    public static String getZodiac(String paramString) {
        return getZodiac(string2Date(paramString, getDefaultFormat()));
    }

    public static String getZodiac(String paramString, DateFormat paramDateFormat) {
        return getZodiac(string2Date(paramString, paramDateFormat));
    }

    public static String getZodiac(Date paramDate) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        return getZodiac(localCalendar.get(2) + 1, localCalendar.get(5));
    }

    public static boolean isAm() {
        return Calendar.getInstance().get(9) == 0;
    }

    public static boolean isAm(long paramLong) {
        return getValueByCalendarField(paramLong, 9) == 0;
    }

    public static boolean isAm(String paramString) {
        return getValueByCalendarField(paramString, getDefaultFormat(), 9) == 0;
    }

    public static boolean isAm(String paramString, DateFormat paramDateFormat) {
        return getValueByCalendarField(paramString, paramDateFormat, 9) == 0;
    }

    public static boolean isAm(Date paramDate) {
        return getValueByCalendarField(paramDate, 9) == 0;
    }

    public static boolean isLeapYear(int paramInt) {
        return ((paramInt % 4 == 0) && (paramInt % 100 != 0)) || (paramInt % 400 == 0);
    }

    public static boolean isLeapYear(long paramLong) {
        return isLeapYear(millis2Date(paramLong));
    }

    public static boolean isLeapYear(String paramString) {
        return isLeapYear(string2Date(paramString, getDefaultFormat()));
    }

    public static boolean isLeapYear(String paramString, DateFormat paramDateFormat) {
        return isLeapYear(string2Date(paramString, paramDateFormat));
    }

    public static boolean isLeapYear(Date paramDate) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        return isLeapYear(localCalendar.get(1));
    }

    public static boolean isPm() {
        return isAm() ^ true;
    }

    public static boolean isPm(long paramLong) {
        return isAm(paramLong) ^ true;
    }

    public static boolean isPm(String paramString) {
        return isAm(paramString) ^ true;
    }

    public static boolean isPm(String paramString, DateFormat paramDateFormat) {
        return isAm(paramString, paramDateFormat) ^ true;
    }

    public static boolean isPm(Date paramDate) {
        return isAm(paramDate) ^ true;
    }

    public static boolean isToday(long paramLong) {
        long l = getWeeOfToday();
        return (paramLong >= l) && (paramLong < l + 86400000L);
    }

    public static boolean isToday(String paramString) {
        return isToday(string2Millis(paramString, getDefaultFormat()));
    }

    public static boolean isToday(String paramString, DateFormat paramDateFormat) {
        return isToday(string2Millis(paramString, paramDateFormat));
    }

    public static boolean isToday(Date paramDate) {
        return isToday(paramDate.getTime());
    }

    public static Date millis2Date(long paramLong) {
        return new Date(paramLong);
    }

    private static String millis2FitTimeSpan(long paramLong, int paramInt) {
        if (paramInt <= 0) {
            return null;
        }
        int i = Math.min(paramInt, 5);
        String[] arrayOfString = new String[5];
        arrayOfString[0] = "天";
        arrayOfString[1] = "小时";
        arrayOfString[2] = "分钟";
        arrayOfString[3] = "秒";
        arrayOfString[4] = "毫秒";
        paramInt = 0;
        if (paramLong == 0L) {
            StringBuilder str = new StringBuilder();
            str.append(0);
            str.append(arrayOfString[(i - 1)]);
            return str.toString();
        }
        StringBuilder localStringBuilder = new StringBuilder();
        long l1 = paramLong;
        if (paramLong < 0L) {
            localStringBuilder.append("-");
            l1 = -paramLong;
        }
        int[] arrayOfInt = new int[5];
        int[] tmp128_126 = arrayOfInt;
        tmp128_126[0] = 86400000;
        int[] tmp134_128 = tmp128_126;
        tmp134_128[1] = 3600000;
        int[] tmp140_134 = tmp134_128;
        tmp140_134[2] = 60000;
        int[] tmp146_140 = tmp140_134;
        tmp146_140[3] = 'Ϩ';
        int[] tmp152_146 = tmp146_140;
        tmp152_146[4] = 1;
        //tmp152_146;
        while (paramInt < i) {
            paramLong = l1;
            if (l1 >= arrayOfInt[paramInt]) {
                long l2 = l1 / arrayOfInt[paramInt];
                paramLong = l1 - arrayOfInt[paramInt] * l2;
                localStringBuilder.append(l2);
                localStringBuilder.append(arrayOfString[paramInt]);
            }
            paramInt += 1;
            l1 = paramLong;
        }
        return localStringBuilder.toString();
    }

    public static String millis2String(long paramLong) {
        return millis2String(paramLong, getDefaultFormat());
    }

    public static String millis2String(long paramLong, String paramString) {
        return millis2String(paramLong, getDateFormat(paramString));
    }

    public static String millis2String(long paramLong, DateFormat paramDateFormat) {
        return paramDateFormat.format(new Date(paramLong));
    }

    private static long millis2TimeSpan(long paramLong, int paramInt) {
        return paramLong / paramInt;
    }

    public static Date string2Date(String paramString) {
        return string2Date(paramString, getDefaultFormat());
    }

    public static Date string2Date(String paramString1, String paramString2) {
        return string2Date(paramString1, getDateFormat(paramString2));
    }

    public static Date string2Date(String paramString, DateFormat dateFormat) {
        try {
            Date date = dateFormat.parse(paramString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long string2Millis(String paramString) {
        return string2Millis(paramString, getDefaultFormat());
    }

    public static long string2Millis(String paramString1, String paramString2) {
        return string2Millis(paramString1, getDateFormat(paramString2));
    }

    public static long string2Millis(String paramString, DateFormat paramDateFormat) {
        try {
            long l = paramDateFormat.parse(paramString).getTime();
            return l;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    private static long timeSpan2Millis(long paramLong, int paramInt) {
        return paramLong * paramInt;
    }
}