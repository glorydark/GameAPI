package gameapi.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author glorydark
 */
public class CalendarTools {

    protected static final String DATE_FORMAT_DETAILED_EASY = "yyyy-MM-dd HH-mm-ss";

    protected static final String DATE_FORMAT_DETAILED = "yyyy年MM月dd日 HH时mm分ss秒";

    protected static final String DATE_FORMAT = "yyyy年MM月dd日";

    protected static final String DATE_FORMAT_SIMPLE = "yyyy/MM/dd日";

    public static final long MILLIS_MINUTE = 60000;
    public static final long MILLIS_HOUR = 3600000;
    public static final long MILLIS_DAY = 86400000;
    public static final long MILLIS_WEEK = 604800000;
    public static final long MILLIS_MONTH = 2592000000L;
    public static final long MILLIS_YEAR = 31536000000L;


    public static String getDateStringByDefault(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_DETAILED);
        return format.format(date);
    }

    public static String getDateStringWithoutDetailsByDefault(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public static String getDateString() {
        return getDateString(DATE_FORMAT_SIMPLE);
    }

    public static String getDateString(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static String getDateString(long time, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    public static Date getDate(String encodedString) {
        return getDate(encodedString, DATE_FORMAT_DETAILED_EASY);
    }

    public static Date getDate(String encodedString, String format) {
        try {
            return new SimpleDateFormat(format).parse(encodedString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
