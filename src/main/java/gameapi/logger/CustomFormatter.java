package gameapi.logger;

import gameapi.tools.SmartTools;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author glorydark
 */
public class CustomFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return "[" + SmartTools.getDate(System.currentTimeMillis()) + "] "
                + record.getMessage() + "\n";
    }
}
