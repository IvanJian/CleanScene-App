package inspiringbits.me.cleanscene.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IvanJian on 2017/8/29.
 */
public class DateTimeTool {
    public static final String DATE_FORMAT="yyyy-MM-dd";
    public static final String TIME_FORMAT="HH:mm:ss";
    public static final String DATE_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static final String TIME_ZONE="GMT+10";
    public static final String BEFORE = "before";
    public static final String EQUAL = "equal";
    public static final String AFTER = "after";

    public static String getCurrentDate(){
        SimpleDateFormat sdfd = new SimpleDateFormat(DATE_FORMAT);
        sdfd.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        String date = sdfd.format(new Date());
        return date;
    }

    public static String getCurrentTime(){
        SimpleDateFormat sdft = new SimpleDateFormat(TIME_FORMAT);
        sdft.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        String time = sdft.format(new Date());
        return time;
    }

    public static String compareDate(String date1Str,String date2Str) throws ParseException {
        SimpleDateFormat sdfd = new SimpleDateFormat(DATE_FORMAT);
        Date date1=sdfd.parse(date1Str);
        Date date2=sdfd.parse(date2Str);
        if (date1.compareTo(date2)<0){
            return BEFORE;
        }
        if (date1.compareTo(date2)==0){
            return EQUAL;
        }
        if (date1.compareTo(date2)>0){
            return AFTER;
        }
        return null;
    }

    public static String compareDateTime(String date1Str,String time1Str,String date2Str,String time2Str) throws ParseException {
        SimpleDateFormat sdfd = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date date1=sdfd.parse(date1Str+" "+time1Str);
        Date date2=sdfd.parse(date2Str+" "+time2Str);
        if (date1.compareTo(date2)<0){
            return BEFORE;
        }
        if (date1.compareTo(date2)==0){
            return EQUAL;
        }
        if (date1.compareTo(date2)>0){
            return AFTER;
        }
        return null;
    }
}
