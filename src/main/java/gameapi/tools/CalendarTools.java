package gameapi.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameapi.GameAPI;
import gameapi.utils.TimestampData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static long getBeijingTimeMillis(long defaultValue) {
        String apiUrl = GameAPI.getInstance().getTimestampApi();
        long startTime = System.currentTimeMillis(); // 记录请求发起的时间
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != 200) {
                GameAPI.getGameDebugManager().printError(new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode()));
                return defaultValue;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            // 解析 JSON
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            TimestampData map = gson.fromJson(response.toString(), TimestampData.class);

            int year = map.getYear();
            int month = map.getMonth();
            int day = map.getDay();
            int hour = map.getHour();
            int minute = map.getMinute();
            int second = map.getSeconds();
            int milliSeconds = map.getMilliSeconds();

            // 转换为时间戳
            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, milliSeconds * 1_000_000);
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");

            long endTime = System.currentTimeMillis(); // 记录接收到数据的时间
            // 计算延迟
            long delay = endTime - startTime; // 延迟单位是毫秒
            return dateTime.atZone(zoneId).toInstant().toEpochMilli() + delay;

        } catch (Throwable t) {
            GameAPI.getGameDebugManager().printError(t);
        }
        return defaultValue;
    }
}
