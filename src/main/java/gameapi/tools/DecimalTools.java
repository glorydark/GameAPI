package gameapi.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author glorydark
 */
public class DecimalTools {

    public static double getFloatDiff(float v1, float v2, int scale) {
        return getFloatDiff(v1, v2, scale, RoundingMode.FLOOR);
    }

    public static double getFloatDiff(float v1, float v2, int scale, RoundingMode roundingMode) {
        return Double.parseDouble(getFloatByFormat(v1 - v2, scale, roundingMode));
    }

    public static double getDoubleDiff(double v1, double v2, int scale) {
        return getDoubleDiff(v1, v2, scale, RoundingMode.FLOOR);
    }

    public static double getDoubleDiff(double v1, double v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).doubleValue();
    }

    public static double getFloatAdd(float v1, float v2, int scale) {
        return getFloatAdd(v1, v2, scale, RoundingMode.FLOOR);
    }

    public static double getFloatAdd(float v1, float v2, int scale, RoundingMode roundingMode) {
        return Double.parseDouble(getFloatByFormat(v1 + v2, scale, roundingMode));
    }

    public static double getDoubleAdd(double v1, double v2, int scale) {
        return getDoubleAdd(v1, v2, scale, RoundingMode.FLOOR);
    }

    public static double getDoubleAdd(double v1, double v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).doubleValue();
    }

    public static double getDouble(double v, int scale) {
        return getDouble(v, scale, RoundingMode.FLOOR);
    }

    public static double getDouble(double v, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v).setScale(scale, roundingMode).doubleValue();
    }

    public static float getFloat(float v, int scale) {
        return getFloat(v, scale, RoundingMode.FLOOR);
    }

    public static float getFloat(float v, int scale, RoundingMode roundingMode) {
        return Float.parseFloat(getFloatByFormat(v, scale, roundingMode));
    }

    public static double getFloatToDouble(float v, int scale) {
        return getFloatToDouble(v, scale, RoundingMode.FLOOR);
    }

    public static double getFloatToDouble(float v, int scale, RoundingMode roundingMode) {
        return Double.parseDouble(getFloatByFormat(v, scale, roundingMode));
    }

    public static void main(String[] args) {
        System.out.println(formatDouble(1.2f, 2));
        System.out.println(formatDouble(1.233f, 2));
        System.out.println(formatDouble(1.235f, 2));
    }

    public static String getFloatByFormat(float value, int decimalPlaces, RoundingMode roundingMode) {
        // 创建一个模式字符串，其中包含所需的小数位数
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            pattern.append("0"); // 每增加一个小数位，就在模式字符串中添加一个 '0'
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setRoundingMode(roundingMode); // 设置舍入模式
        return df.format(value);
    }

    public static String formatFloat(float v, int scale) {
        // 处理负数
        boolean negative = v < 0;
        v = Math.abs(v);

        // 使用float的数学运算
        float factor = (float) Math.pow(10, scale);
        float rounded = Math.round(v * factor) / factor;

        // 分割整数和小数
        int intPart = (int) rounded;
        int fracPart = Math.round((rounded - intPart) * factor);

        // 构建字符串
        StringBuilder sb = new StringBuilder();
        if (negative) sb.append('-');
        sb.append(intPart).append('.');

        // 小数补零
        String fracStr = Integer.toString(fracPart);
        sb.append("0".repeat(Math.max(0, scale - fracStr.length())));
        sb.append(fracStr);

        return sb.toString();
    }

    public static String formatDouble(double v, int scale) {
        if (scale == 0) {
            return String.valueOf(Math.round(v));
        }

        // 快速处理
        double factor = Math.pow(10, scale);
        long value = Math.round(v * factor);
        long intPart = value / (long) factor;
        long fracPart = Math.abs(value % (long) factor);

        StringBuilder sb = new StringBuilder();
        sb.append(intPart).append('.');

        // 快速补零
        String frac = String.valueOf(fracPart);
        int zerosToAdd = scale - frac.length();
        sb.append("0".repeat(Math.max(0, zerosToAdd)));
        sb.append(frac);

        return sb.toString();
    }
}
