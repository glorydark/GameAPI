package gameapi.logger;

import gameapi.tools.CalendarTools;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author glorydark
 */
public class CustomFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return "[" + CalendarTools.getDateStringByDefault(System.currentTimeMillis()) + "] "
                + record.getMessage() + "\n";
    }
}
