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

    public static Date getDate(String encodedString) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_DETAILED_EASY).parse(encodedString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
