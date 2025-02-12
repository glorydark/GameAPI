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
    }public static double getDoubleDiff(double v1, double v2, int scale) {
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

    public static double getFloatToDouble(float v, int scale) {
        return getFloatToDouble(v, scale, RoundingMode.FLOOR);
    }

    public static double getFloatToDouble(float v, int scale, RoundingMode roundingMode) {
        return Double.parseDouble(getFloatByFormat(v, scale, roundingMode));
    }

    public static void main(String[] args) {
        System.out.println(getFloatToDouble(1.6099999f, 1));
        System.out.println(getFloatByFormat(1.6099999f, 1, RoundingMode.FLOOR));
    }

    protected static String getFloatByFormat(float value, int decimalPlaces, RoundingMode roundingMode) {
        // 创建一个模式字符串，其中包含所需的小数位数
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < decimalPlaces; i++) {
            pattern.append("0"); // 每增加一个小数位，就在模式字符串中添加一个 '0'
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setRoundingMode(roundingMode); // 设置舍入模式
        return df.format(value);
    }
}
