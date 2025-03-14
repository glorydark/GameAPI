package gameapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@AllArgsConstructor
@Data
public class TimestampData {

    int year;
    int month;
    int day;
    int hour;
    int minute;
    int seconds;
    int milliSeconds;
    String dateTime;
    String date;
    String time;
    String timeZone;
    String dayOfWeek;
    boolean dstActive;
}
