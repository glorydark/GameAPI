package gameapi;

import gameapi.tools.CalendarTools;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Calendar;

/**
 * @author glorydark
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(BigDecimal.valueOf(18 / 20d).setScale(1, RoundingMode.FLOOR).doubleValue());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);
        System.out.println(CalendarTools.getWeekString(calendar));

        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 5);
        System.out.println(CalendarTools.getWeekString(calendar));

        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 12);
        System.out.println(CalendarTools.getWeekString(calendar));
    }
}
