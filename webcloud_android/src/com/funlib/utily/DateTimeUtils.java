package com.funlib.utily;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {
    public static final int DATETIME_FIELD_DATE = 0;
    
    public static final int DATETIME_FIELD_DATETIMEWITHOUTSECOND = 10;
    
    public static final int DATETIME_FIELD_DATEWITHOUTDAY = 11;
    
    public static final int DATETIME_FIELD_DATEWITHOUTYEAR = 1;
    
    public static final int DATETIME_FIELD_DAYOFMONTH = 4;
    
    public static final int DATETIME_FIELD_HOUROFDAY = 7;
    
    public static final int DATETIME_FIELD_MINUTE = 8;
    
    public static final int DATETIME_FIELD_MONTH = 3;
    
    public static final int DATETIME_FIELD_REFERSH = 20;
    
    public static final int DATETIME_FIELD_SECOND = 9;
    
    public static final int DATETIME_FIELD_TIME = 5;
    
    public static final int DATETIME_FIELD_TIMEWITHOUTSECOND = 6;
    
    public static final int DATETIME_FIELD_YEAR = 2;
    
    public static final int DATETIME_FIELD_YEAR_SHORT = 12;
    
    public static final long ONE_HOUR = 3600000L;
    
    public static final String REGEX = "\\d{1,4}";
    
    /**返回月日短格式。
     * format:05月09日
     * @param paramCalendar
     * @return
     */
    public static String calendarToXyueXri(Calendar paramCalendar) {
        String str = "";
        if (paramCalendar != null) {
            int i = 1 + paramCalendar.get(2);
            if (i >= 10)
                str = i + "月";
            else
                str = "0" + i + "月";
            int j = paramCalendar.get(5);
            if (j >= 10)
                str = str + j + "日";
            else
                str = str + "0" + j + "日";
        } else {
            str = null;
        }
        return str;
    }
    
    public static String changeDataTimeStringToSC(String paramString) {
        return formatCalToSCString(formatStringToCalendar(paramString));
    }
    
    public static String changeDateStringToSC(String paramString) {
        String str;
        if ((paramString != null) && (paramString != "")) {
            String[] arrayOfString = trim(paramString.split("-"));
            for (int i = 0; i < 3; i++) {
                if (arrayOfString[i].length() != 1)
                    continue;
                arrayOfString[i] = ("0" + arrayOfString[i]);
            }
            str = arrayOfString[0] + "年" + arrayOfString[1] + "月" + arrayOfString[2] + "日";
        } else {
            str = paramString;
        }
        return str;
    }
    
    private static void checkArrayNullOrEmpty(Object[] arr) {
        if (arr != null) {
            if (arr.length != 0)
                return;
            throw new IllegalArgumentException("array must have some datas");
        }
        throw new NullPointerException();
    }
    
    public static String fixDateString(String paramString) {
        String[] strs;
        String result;
        if ((paramString != null) && (paramString != "")) {
            strs = trim(paramString.split("[年月日]"));
            for (int i = 0; i < 3; i++) {
                if (strs[i].length() != 1)
                    continue;
                strs[i] = ("0" + strs[i]);
            }
            result = strs[0] + "-" + strs[1] + "-" + strs[2];
        } else {
            result = paramString;
        }
        return result;
    }
    
    public static String fixDateStringWithoutYear(String paramString) {
        String str;
        if ((paramString != null) && (paramString != "")) {
            String[] arrayOfString = trim(paramString.split("[年月日]"));
            for (int i = 0; i < 3; i++) {
                if (arrayOfString[i].length() != 1)
                    continue;
                arrayOfString[i] = ("0" + arrayOfString[i]);
            }
            str = arrayOfString[1] + "-" + arrayOfString[2];
        } else {
            str = "";
        }
        return str;
    }
    
    /** 
     * 传入日历，返回
     * 2011年08月20日
     *
     * @param car
     * @return
     */
    public static String formatCalToSCString(Calendar car) {
        String str;
        if (car != null) {
            str = String.valueOf(car.get(1)) + "年";
            if (1 + car.get(2) >= 10)
                str = str + String.valueOf(1 + car.get(2)) + "月";
            else
                str = str + "0" + String.valueOf(1 + car.get(2)) + "月";
            if (car.get(5) >= 10)
                str = str + String.valueOf(car.get(5)) + "日";
            else
                str = str + "0" + String.valueOf(car.get(5)) + "日";
        } else {
            str = null;
        }
        return str;
    }
    
    /** 格式化日历。
     * 2012-09-10 03:10
     * yyyy-MM-dd HH:mi
     * @param paramCalendar
     * @return
     */
    public static String formatCalendarToMiddleString(Calendar paramCalendar) {
        //年
        String str = String.valueOf(paramCalendar.get(1)) + "-";
        //月
        if (1 + paramCalendar.get(2) >= 10)
            str = str + String.valueOf(1 + paramCalendar.get(2)) + "-";
        else
            str = str + "0" + String.valueOf(1 + paramCalendar.get(2)) + "-";
        //日
        if (paramCalendar.get(5) >= 10)
            str = str + String.valueOf(paramCalendar.get(5));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(5));
        str = str + " ";
        //时
        if (paramCalendar.get(11) >= 10)
            str = str + String.valueOf(paramCalendar.get(11)) + ":";
        else
            str = str + "0" + String.valueOf(paramCalendar.get(11)) + ":";
        //分
        if (paramCalendar.get(12) >= 10)
            str = str + String.valueOf(paramCalendar.get(12));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(12));
        //秒
        /*if (paramCalendar.get(13) >= 10)
            str = str + String.valueOf(paramCalendar.get(13));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(13));*/
        return str;
    }
    
    /** 格式化日历。
     * 03:10
     * @param paramCalendar
     * @return
     */
    public static String formatCalendarHourMinute(Calendar paramCalendar) {
        String str = "";
        //时
        if (paramCalendar.get(11) >= 10)
            str = str + String.valueOf(paramCalendar.get(11)) + ":";
        else
            str = str + "0" + String.valueOf(paramCalendar.get(11)) + ":";
        //分
        if (paramCalendar.get(12) >= 10)
            str = str + String.valueOf(paramCalendar.get(12));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(12));
        return str;
    }
    
    public static String formatCalendarToString(Calendar paramCalendar) {
        String str = String.valueOf(paramCalendar.get(1)) + "-";
        if (1 + paramCalendar.get(2) >= 10)
            str = str + String.valueOf(1 + paramCalendar.get(2)) + "-";
        else
            str = str + "0" + String.valueOf(1 + paramCalendar.get(2)) + "-";
        if (paramCalendar.get(5) >= 10)
            str = str + String.valueOf(paramCalendar.get(5));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(5));
        str = str + " ";
        if (paramCalendar.get(11) >= 10)
            str = str + String.valueOf(paramCalendar.get(11)) + ":";
        else
            str = str + "0" + String.valueOf(paramCalendar.get(11)) + ":";
        if (paramCalendar.get(12) >= 10)
            str = str + String.valueOf(paramCalendar.get(12)) + ":";
        else
            str = str + "0" + String.valueOf(paramCalendar.get(12)) + ":";
        if (paramCalendar.get(13) >= 10)
            str = str + String.valueOf(paramCalendar.get(13));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(13));
        return str;
    }
    
    /** 返回标准日期格式
     * format: 2011-05-09
     * @param paramCalendar
     * @return
     */
    public static String formatCalendarToShortString(Calendar paramCalendar) {
        String str = String.valueOf(paramCalendar.get(1)) + "-";
        if (1 + paramCalendar.get(2) >= 10)
            str = str + String.valueOf(1 + paramCalendar.get(2)) + "-";
        else
            str = str + "0" + String.valueOf(1 + paramCalendar.get(2)) + "-";
        if (paramCalendar.get(5) >= 10)
            str = str + String.valueOf(paramCalendar.get(5));
        else
            str = str + "0" + String.valueOf(paramCalendar.get(5));
        return str;
    }
    
    /** 返回 yyyy年MM月dd日 格式
     * @param paramDate
     * @return
     */
    public static String formatDateToSCString(Date paramDate) {
        return new SimpleDateFormat("yyyy年MM月dd日").format(paramDate);
    }
    
    /** 返回 yyyy-MM-dd 格式
     * @param paramDate
     * @return
     */
    public static String formatDateToString(Date paramDate) {
        return new SimpleDateFormat("yyyy-MM-dd").format(paramDate);
    }
    
    public static String formatDateToStringByFMT(Date paramDate, String fmt) {
        return new SimpleDateFormat(fmt).format(paramDate);
    }
    
    public static Calendar formatSCStringToCalendar(String paramString) {
        Calendar localCalendar;
        if ((paramString != null) && (paramString != ""))
            localCalendar = formatStringToCalendar(fixDateString(paramString));
        else
            localCalendar = null;
        return localCalendar;
    }
    
    public static Date formatSCStringToDate(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date localDate = null;
        try {
            localDate = localSimpleDateFormat.parse(paramString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDate;
    }
    
    /**
     * 格式化日期字符串到日历。 
     * 
     * @param dateStr 2013-02-11
     * @return
     */
    public static Calendar formatStringToCalendar(String dateStr) {
        Calendar car = Calendar.getInstance();
        if ((dateStr != null) && (dateStr != "")) {
            car.setLenient(true);
            List<String> dateArr = new ArrayList<String>();
            //匹配1-4位整数
            Matcher matcher = Pattern.compile("\\d{1,4}").matcher(dateStr);
            while (matcher.find())
                dateArr.add(matcher.group());
            //非yyyy-MM-dd格式
            if (dateArr.size() != 3) {
                if (dateArr.size() == 6 || dateArr.size() == 7)
                    car.set(Integer.parseInt((String)dateArr.get(0)),
                        Integer.parseInt((String)dateArr.get(1)) - 1,
                        Integer.parseInt((String)dateArr.get(2)),
                        Integer.parseInt((String)dateArr.get(3)),
                        Integer.parseInt((String)dateArr.get(4)),
                        Integer.parseInt((String)dateArr.get(5)));
            } else
                car.set(Integer.parseInt((String)dateArr.get(0)),
                    Integer.parseInt((String)dateArr.get(1)) - 1,
                    Integer.parseInt((String)dateArr.get(2)),
                    0,
                    0,
                    0);
        }
        return car;
    }
    
    /** 
     * @param paramString
     * @return
     */
    public static Date formatStringToDate(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date localDate = null;
        try {
            localDate = localSimpleDateFormat.parse(paramString);
            return localDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDate;
    }
    
    /** 
     * @param paramString
     * @return
     */
    public static String formatDate(String paramString) {
    	String dateStr = "";
    	if (paramString != null) {
			if (paramString.contains(" ")) {
				dateStr = paramString.split(" ")[0];
			} else {
				dateStr = paramString;
			}
		} 
        return dateStr;
    }
    
    
    public static int getBirthIntervalDays(Calendar paramCalendar1, Calendar paramCalendar2) {
        int i;
        if ((paramCalendar1 != null) && (paramCalendar2 != null)) {
            if (paramCalendar1.after(paramCalendar2)) {
                Calendar localCalendar = paramCalendar1;
                paramCalendar1 = paramCalendar2;
                paramCalendar2 = localCalendar;
            }
            i =
                (int)((3600000L + (paramCalendar2.getTime().getTime() - paramCalendar1.getTime().getTime())) / 86400000L);
        } else {
            i = 0;
        }
        return i;
    }
    
    public static Calendar getCurrentDateTime() {
        return Calendar.getInstance();
    }
    
    public static long getDaysBetween(String paramString1, String paramString2) {
        Calendar localCalendar = formatStringToCalendar(paramString1);
        return Math.abs(formatStringToCalendar(paramString2).getTime().getTime() - localCalendar.getTime().getTime()) / 86400000L;
    }
    
    /** 
     * 返回两日历间隔时间
     *
     * @param c1
     * @param c2
     * @return 毫秒
     */
    public static long getMillBetween(Calendar c1,Calendar c2){
        return (c1.getTime().getTime() - c2.getTime().getTime());
    }
    
    /** 返回两个日期相差天数。
     * 后面的日期-前面的日期
     * @param d1
     * @param d2
     * @return
     * @throws ParseException
     */
    public static Long getDaysBetween(Date d1, Date d2)
        throws ParseException {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        c1.set(11, 0);
        c1.set(12, 0);
        c1.set(13, 0);
        c1.set(14, 0);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        c2.set(11, 0);
        c2.set(12, 0);
        c2.set(13, 0);
        c2.set(14, 0);
        return Long.valueOf(Math.abs(c2.getTime().getTime() - c1.getTime().getTime()) / 86400000L);
    }
    
    public static Calendar getDefaultDateTimeFromDateTime(String paramString1, String paramString2, int paramInt) {
        Object localObject;
        if ((paramString1 != null) && (paramString1.length() != 0) && (paramString2 != null)
            && (paramString2.length() != 0)) {
            localObject = formatStringToCalendar(paramString1);
            Calendar localCalendar = formatStringToCalendar(paramString2);
            if (!localCalendar.before(localObject)) {
                localObject = localCalendar;
            } else {
                ((Calendar)localObject).add(5, paramInt);
            }
        } else {
            localObject = null;
        }
        return (Calendar)localObject;
    }
    
    public static Calendar getDefaultDateTimeFromDateTime(String paramString, Calendar paramCalendar, int paramInt) {
        Calendar localCalendar1;
        if ((paramString != null) && (paramString.length() != 0) && (paramCalendar != null)) {
            Calendar localCalendar2 = formatStringToCalendar(paramString);
            localCalendar1 = (Calendar)paramCalendar.clone();
            if (!localCalendar1.before(localCalendar2)) {
            } else {
                localCalendar2.add(5, paramInt);
                localCalendar1 = localCalendar2;
            }
        } else {
            localCalendar1 = null;
        }
        return localCalendar1;
    }
    
    public static Calendar getDefaultDateTimeFromDateTime(Calendar paramCalendar, String paramString, int paramInt) {
        Object localObject;
        if ((paramCalendar != null) && (paramString != null) && (paramString.length() != 0)) {
            localObject = (Calendar)paramCalendar.clone();
            Calendar localCalendar = formatStringToCalendar(paramString);
            if (!localCalendar.before(localObject)) {
                localObject = localCalendar;
            } else {
                ((Calendar)localObject).add(5, paramInt);
            }
        } else {
            localObject = null;
        }
        return (Calendar)localObject;
    }
    
    public static Calendar getDefaultDateTimeFromDateTime(Calendar paramCalendar1, Calendar paramCalendar2, int paramInt) {
        Calendar localCalendar;
        if ((paramCalendar1 != null) && (paramCalendar2 != null)) {
            localCalendar = (Calendar)paramCalendar1.clone();
            if (!paramCalendar2.before(paramCalendar1)) {
                localCalendar = paramCalendar2;
            } else {
                localCalendar.add(5, paramInt);
            }
        } else {
            localCalendar = null;
        }
        return localCalendar;
    }
    
    public static String getFieldStringFromCalendar(Calendar paramCalendar, int paramInt) {
        String str = "";
        switch (paramInt) {
            case 0:
                str = formatCalendarToString(paramCalendar).substring(0, 10);
                break;
            case 1:
                str = formatCalendarToString(paramCalendar).substring(5, 10);
                break;
            case 2:
                str = formatCalendarToString(paramCalendar).substring(0, 4);
                break;
            case 3:
                str = formatCalendarToString(paramCalendar).substring(5, 7);
                break;
            case 4:
                str = formatCalendarToString(paramCalendar).substring(8, 10);
                break;
            case 5:
                str = formatCalendarToString(paramCalendar).substring(11, 19);
                break;
            case 6:
                str = formatCalendarToString(paramCalendar).substring(11, 16);
                break;
            case 7:
                str = formatCalendarToString(paramCalendar).substring(11, 13);
                break;
            case 8:
                str = formatCalendarToString(paramCalendar).substring(14, 16);
                break;
            case 9:
                str = formatCalendarToString(paramCalendar).substring(17, 19);
                break;
            case 10:
                str = formatCalendarToString(paramCalendar).substring(0, 16);
                break;
            case 11:
                str = formatCalendarToString(paramCalendar).substring(0, 7);
                break;
            case 12:
                str = formatCalendarToString(paramCalendar).substring(2, 4);
        }
        return str;
    }
    
    public static String getFieldStringFromDateString(String paramString, int paramInt) {
        String str;
        if (paramString != null)
            str = getFieldStringFromCalendar(formatStringToCalendar(paramString), paramInt);
        else
            str = "";
        return str;
    }
    
    public static Calendar getIntervalDateTime(String paramString, int paramInt) {
        Calendar localCalendar;
        if ((paramString != null) && (paramString.length() != 0))
            localCalendar = getIntervalDateTime(formatStringToCalendar(paramString), paramInt);
        else
            localCalendar = null;
        return localCalendar;
    }
    
    public static Calendar getIntervalDateTime(Calendar paramCalendar, int paramInt) {
        Calendar localCalendar;
        if (paramCalendar != null) {
            localCalendar = (Calendar)paramCalendar.clone();
            localCalendar.add(5, paramInt);
        } else {
            localCalendar = null;
        }
        return localCalendar;
    }
    
    public static int getIntervalDays(String paramString1, String paramString2) {
        int i;
        if ((paramString1 != null) && (paramString2 != null))
            i = getIntervalDays(formatStringToCalendar(paramString1), formatStringToCalendar(paramString2));
        else
            i = 0;
        return i;
    }
    
    public static int getIntervalDays(Calendar paramCalendar1, Calendar paramCalendar2) {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.set(paramCalendar1.get(1), paramCalendar1.get(2), paramCalendar1.get(5), 0, 0, 0);
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.set(paramCalendar2.get(1), paramCalendar2.get(2), paramCalendar2.get(5), 0, 0, 0);
        return (int)(Math.abs(localCalendar1.getTimeInMillis() - localCalendar2.getTimeInMillis()) / 86400000L);
    }
    
    public static String getToDateStr() {
        Calendar localCalendar = getCurrentDateTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(localCalendar.getTime());
    }
    
    public static String getWeekDayFromCalendar(Calendar paramCalendar) {
        String str = "";
        if (paramCalendar != null) {
            switch (paramCalendar.get(7)) {
                case 1:
                    str = str + "周日";
                    break;
                case 2:
                    str = str + "周一";
                    break;
                case 3:
                    str = str + "周二";
                    break;
                case 4:
                    str = str + "周三";
                    break;
                case 5:
                    str = str + "周四";
                    break;
                case 6:
                    str = str + "周五";
                    break;
                case 7:
                    str = str + "周六";
            }
        }
        return str;
    }
    
    public static String getWeekDayFromCalendar1(Calendar paramCalendar) {
        String str = "";
        if (paramCalendar != null) {
            switch (paramCalendar.get(7)) {
                case 1:
                    str = str + "星期天";
                    break;
                case 2:
                    str = str + "星期一";
                    break;
                case 3:
                    str = str + "星期二";
                    break;
                case 4:
                    str = str + "星期三";
                    break;
                case 5:
                    str = str + "星期四";
                    break;
                case 6:
                    str = str + "星期五";
                    break;
                case 7:
                    str = str + "星期六";
            }
        }
        return str;
    }
    
    public static boolean isLeapyear(String paramString) {
        boolean i = false;
        if ((!paramString.contains("-")) && ((!paramString.contains("年")) || (!paramString.contains("月")))) {
            i = false;
        } else {
            int year = Integer.parseInt(paramString.substring(0, 3));
            if (((year % 100 != 0) || (year % 400 != 0)) && ((year % 100 == 0) || (year % 4 != 0)))
                i = false;
            else
                i = true;
        }
        return i;
    }
    
    public static boolean isRefersh(long paramLong) {
        boolean isRefresh = false;
        if (new Date().getTime() - paramLong < 1200000L)
            isRefresh = false;
        else
            isRefresh = true;
        return isRefresh;
    }
    
    public static boolean isToDay(String paramString) {
        boolean isToDay = false;
        if (!getToDateStr().equals(paramString))
            isToDay = false;
        else
            isToDay = true;
        return isToDay;
    }
    
    private static String[] trim(String[] strArr) {
        checkArrayNullOrEmpty(strArr);
        int i = strArr.length;
        for (int j = 0; j < i; j++)
            strArr[j] = strArr[j].trim();
        return strArr;
    }
    
    /** 比较两个日期，当第一个参数大于第二个参数，返回false
     * @param lastDate
     * @param addDate
     * @return
     */
    public static boolean isToUpdate(String lastDate, String addDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(lastDate);
            Date dt2 = df.parse(addDate);
            if (dt1.getTime() > dt2.getTime()) {
                return false;
            } else if (dt1.getTime() < dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
    
    public static String getNowTime(String dateFormatStr) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);// 可以方便地修改日期格式
        String nowTime = dateFormat.format(now);
        return nowTime;
    }
    
    public static String getFormatDateStr(String formatStr, String dateStr) {
        String dates;
        try {
            DateFormat dateFormat = new SimpleDateFormat(formatStr);// 可以方便地修改日期格式
            Date date = dateFormat.parse(dateStr);
            dates = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dates = "";
        }
        return dates;
    }
    
    public final static String getAddDate(String oldDate, int num) {
        Calendar cal = Calendar.getInstance();
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式
        try {
            date = dateFormat.parse(oldDate);
            //date = DateFormat.getDateInstance().parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, num);
        String newDate = dateFormat.format(cal.getTime());
        return newDate;
    }
    
    public static final int MORNING = 0;
    
    public static final int AFTERNOON = 1;
    
    public static final int NIGHT = -1;
    
    public static String getTimeInDay(int timeFlag) {
        String timeStr;
        switch (timeFlag) {
            case MORNING:
                timeStr = "上午";
                break;
            case AFTERNOON:
                timeStr = "下午";
                break;
            case NIGHT:
                timeStr = "晚上";
                break;
            default:
                timeStr = "无";
                break;
        }
        return timeStr;
    }
    
    public static int compareShortTime(String time) {
        String morning = "06:00";
        String noon = "12:00";
        String night = "18:00";
        if (morning.compareTo(time) <= 0 && noon.compareTo(time) > 0) {
            //早上
            return MORNING;
        } else if (noon.compareTo(time) <= 0 && night.compareTo(time) > 0) {
            //中午
            return AFTERNOON;
        } else if (night.compareTo(time) < 0 || morning.compareTo(time) >= 0) {
            //晚上
            return NIGHT;
        }
        return -999;
    }
    
}
