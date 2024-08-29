package gameapi.tools;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

public class SmartTools {

    public static float timeDiffToFloat(long m1, long m2, int scale) {
        return BigDecimal.valueOf(Math.abs(m1 - m2) / 1000f).setScale(scale, RoundingMode.CEILING).floatValue();
    }

    public static String timeDiffMillisToString(long m1, long m2) {
        return timeDiffMillisToString(m1, m2, true);
    }

    public static String timeDiffMillisToString(long m1, long m2, boolean saveMillis) {
        long diff = Math.abs(m2 - m1);
        return timeMillisToString(diff, saveMillis);
    }

    public static String timeMillisToString(long diff) {
        return timeMillisToString(diff, true);
    }

    public static String timeMillisToString(long diff, boolean saveMillis) {
        long hour = diff / 3600000;
        long minute = diff / 60000 - hour * 60;
        long second = (diff - hour * 3600000 - minute * 60000) / 1000;
        long millis = (diff - hour * 3600000 - minute * 60000 - second * 1000);
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            if (hour < 10) {
                sb.append("0").append(hour).append(":");
            } else {
                sb.append(hour).append(":");
            }
        }
        if (minute > 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append(":");
            } else {
                sb.append(minute).append(":");
            }
        } else {
            sb.append("00:");
        }
        if (second > 0) {
            if (second < 10) {
                sb.append("0").append(second).append(":");
            } else {
                sb.append(second).append(":");
            }
        } else if (second == 0) {
            if (saveMillis) {
                sb.append("00:");
            } else {
                sb.append("00");
                return sb.toString();
            }
        }
        if (millis > 0) {
            if (millis > 100) {
                sb.append(millis);
            } else {
                if (millis > 10) {
                    sb.append("0").append(millis);
                } else {
                    sb.append("00").append(millis);
                }
            }
        } else {
            sb.append("000");
        }
        return sb.toString();
    }

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    public static String dateToString(Date date) {
        return dateToString(date, "yyyy-MM-dd-HH-mm-ss");
    }

    public static String dateToString(Date date, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    //https://blog.csdn.net/weixin_39975055/article/details/115082818
    @Deprecated
    public static Date stringToDate(String string) {
        return stringToDate(string, "yyyy-MM-dd-HH-mm-ss");
    }

    public static Date stringToDate(String string, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        try {
            date = format.parse(string);
        } catch (Exception e) {
            GameAPI.getGameDebugManager().error(e.toString());
        }
        return date;
    }

    public static boolean isInRange(int min, int max, int compare) {
        return Math.max(min, compare) == Math.min(compare, max);
    }

    /**
     * This is a method to get the ordinal string by a number.
     *
     * @param number       大于0的数字
     * @param isAllCapital 是否全部大写
     * @return 序数词字符串
     */
    public static String getOrdinalString(int number, boolean isAllCapital) {
        switch (number) {
            case 1:
                if (isAllCapital) {
                    return "1ST";
                } else {
                    return "1st";
                }
            case 2:
                if (isAllCapital) {
                    return "2ND";
                } else {
                    return "2nd";
                }
            case 3:
                if (isAllCapital) {
                    return "3RD";
                } else {
                    return "3rd";
                }
            default:
                if (number < 0) {
                    return "Invalid Number";
                }
                if (isAllCapital) {
                    return number + "TH";
                } else {
                    return number + "th";
                }
        }
    }

    public <T> List<T> buildList(Supplier<List<T>> supplier) {
        return supplier.get();
    }

    public static String getDate(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return format.format(date);
    }

    public static String getDateWithoutDetails(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public static String getCountdownText(int current, int max, int maxBlockCount, String occupied, String unoccupied, boolean reverse) {
        int replacedBlockCount = new BigDecimal(current).divide(new BigDecimal(max), 2, RoundingMode.CEILING).multiply(new BigDecimal(maxBlockCount)).intValue();
        if (replacedBlockCount > maxBlockCount) {
            replacedBlockCount = maxBlockCount;
        } else if (replacedBlockCount < 0) {
            replacedBlockCount = 0;
        }
        if (replacedBlockCount == maxBlockCount) {
            StringBuilder signResult = new StringBuilder();
            for (int i = 0; i < maxBlockCount; i++) {
                signResult.append(occupied);
            }
            return signResult.toString();
        } else if (replacedBlockCount > 0) {
            StringBuilder signResult = new StringBuilder();
            for (int i = 1; i <= maxBlockCount; i++) {
                if (reverse) {
                    if (i <= replacedBlockCount) {
                        signResult.insert(0, occupied);
                    } else {
                        signResult.insert(0, unoccupied);
                    }
                } else {
                    if (i <= replacedBlockCount) {
                        signResult.append(occupied);
                    } else {
                        signResult.append(unoccupied);
                    }
                }
            }
            return signResult.toString();
        } else {
            StringBuilder signResult = new StringBuilder();
            for (int i = 0; i < maxBlockCount; i++) {
                signResult.append(unoccupied);
            }
            return signResult.toString();
        }
    }

    @Internal
    public static String getCountdownText(Room room, Player player) {
        String yellow = GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.sign.before");
        String red = GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.sign.after.ready");
        String grey = GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.sign.after");
        int currentTime = room.getTime();
        int totalTime = room.getGameWaitTime();
        if (currentTime == totalTime) {
            StringBuilder signResult = new StringBuilder();
            for (int i = 0; i < totalTime; i++) {
                signResult.append(grey);
            }
            return GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.format", signResult, 0);
        } else if (currentTime > 0) {
            StringBuilder signResult = new StringBuilder();
            for (int i = 1; i <= totalTime; i++) {
                if (i <= currentTime) {
                    signResult.insert(0, grey);
                } else {
                    if (totalTime - currentTime <= 3) {
                        signResult.insert(0, red);
                    } else {
                        signResult.insert(0, yellow);
                    }
                }
            }
            return GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.format", signResult, totalTime - currentTime);
        } else {
            StringBuilder signResult = new StringBuilder();
            for (int i = 0; i < totalTime; i++) {
                signResult.append(yellow);
            }
            return GameAPI.getLanguage().getTranslation(player, "room.actionbar.readyStart.countdown.format", signResult, totalTime - currentTime);
        }
    }
}
