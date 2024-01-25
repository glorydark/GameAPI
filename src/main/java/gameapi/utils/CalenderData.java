package gameapi.utils;

import java.util.Calendar;

/**
 * @author glorydark
 */
public class CalenderData {

    protected Calendar calendar;

    private CalenderData() {
        this(System.currentTimeMillis());
    }

    private CalenderData(long millis) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(millis);
    }

    public CalenderData addMillis(int value) {
        this.calendar.add(Calendar.MILLISECOND, value);
        return this;
    }

    public CalenderData reduceMillis(int value) {
        this.calendar.add(Calendar.MILLISECOND, -value);
        return this;
    }

    public CalenderData addSecond(int value) {
        this.calendar.add(Calendar.SECOND, value);
        return this;
    }

    public CalenderData reduceSecond(int value) {
        this.calendar.add(Calendar.SECOND, -value);
        return this;
    }

    public CalenderData addMinute(int value) {
        this.calendar.add(Calendar.MINUTE, value);
        return this;
    }

    public CalenderData reduceMinute(int value) {
        this.calendar.add(Calendar.MINUTE, -value);
        return this;
    }

    public CalenderData addHour(int value) {
        this.calendar.add(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public CalenderData reduceHour(int value) {
        this.calendar.add(Calendar.HOUR_OF_DAY, -value);
        return this;
    }

    public CalenderData addDay(int value) {
        this.calendar.add(Calendar.DAY_OF_YEAR, value);
        return this;
    }

    public CalenderData reduceDay(int value) {
        this.calendar.add(Calendar.DAY_OF_YEAR, -value);
        return this;
    }

    public CalenderData addMonth(int value) {
        this.calendar.add(Calendar.MONTH, value);
        return this;
    }

    public CalenderData reduceMonth(int value) {
        this.calendar.add(Calendar.MONTH, -value);
        return this;
    }

    public CalenderData addYear(int value) {
        this.calendar.add(Calendar.YEAR, value);
        return this;
    }

    public CalenderData reduceYear(int value) {
        this.calendar.add(Calendar.YEAR, -value);
        return this;
    }

    public CalenderData get() {
        return this;
    }
}
