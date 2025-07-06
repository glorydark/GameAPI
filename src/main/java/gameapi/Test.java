package gameapi;

import gameapi.utils.ItemIDSunName;

import java.io.File;

/**
 * @author glorydark
 */
public class Test {

    public static void main(String[] args) {
        /*
        System.out.println(BigDecimal.valueOf(18 / 20d).setScale(1, RoundingMode.FLOOR).doubleValue());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);
        System.out.println(CalendarTools.getWeekString(calendar));

        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 5);
        System.out.println(CalendarTools.getWeekString(calendar));

        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 12);
        System.out.println(CalendarTools.getWeekString(calendar));
         */

        for (ItemIDSunName value : ItemIDSunName.values()) {
            File file = new File("D:/VanillaResourcePack/" + value.getPath());
            if (!file.exists()) {
                System.out.println("Error: " + value.getPath());
            }
        }
    }
}
