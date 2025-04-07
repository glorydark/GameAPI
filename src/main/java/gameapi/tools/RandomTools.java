package gameapi.tools;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomTools {

    private static final Pattern RANDOM_NUMBER_COMPILER = Pattern.compile("rand\\((\\d+),\\s*(\\d+)\\)");

    public static int getRandom(int min, int max) {
        if (max == 0) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return getRand((max - min) + 1) + min;
    }

    public static int getSimpleRandom(int min, int max) {
        if (max == 0) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return new Random().nextInt(max - min + 1) + min;
    }

    public static double getRandom(double min, double max) {
        if (max == 0.0d) {
            return 0.0d;
        }
        if (min == max) {
            return min;
        }
        return min + ((max - min) * new Random().nextDouble());
    }

    public static boolean randTrue(int num) {
        int pin = getRand(10001);
        return pin <= num;
    }

    public static int getRand(int bound) {
        int pin;
        try {
            pin = SecureRandom.getInstance("SHA1PRNG").nextInt(bound);
        } catch (NoSuchAlgorithmException e) {
            pin = ThreadLocalRandom.current().nextInt(bound);
        }
        return pin;
    }

    public static Integer getInt(String var, boolean UInt) {
        Integer found = null;
        try {
            found = Integer.valueOf(var);
            if (UInt) {
                if (found < 0) {
                    found = null;
                }
            }
        } catch (NumberFormatException ignored) {
        }
        return found;
    }

    public static String replaceRandomNumbers(String input) {
        // 正则表达式来匹配rand(x, y)
        Matcher matcher = RANDOM_NUMBER_COMPILER.matcher(input);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            // 提取数字
            int lowerBound = Integer.parseInt(matcher.group(1));
            int upperBound = Integer.parseInt(matcher.group(2));

            // 生成随机数
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int randomNumber = lowerBound + random.nextInt(upperBound - lowerBound + 1);

            // 将匹配到的部分替换为随机数
            matcher.appendReplacement(sb, String.valueOf(randomNumber));
        }
        // 将最后的部分追加到StringBuffer
        matcher.appendTail(sb);

        return sb.toString();
    }
}
