package gameapi.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author glorydark
 */
public class CalendarTools {

    public String getDateString() {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }
}
