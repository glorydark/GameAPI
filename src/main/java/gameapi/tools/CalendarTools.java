package gameapi.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author glorydark
 */
public class CalendarTools {

    public static String getDateStringByDefault(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return format.format(date);
    }

    public static String getDateStringWithoutDetailsByDefault(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public static String getDateString() {
        return getDateString("yyyy/MM/dd");
    }

    public static String getDateString(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }
}
