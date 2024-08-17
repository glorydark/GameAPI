package gameapi.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author glorydark
 */
public class DecimalTools {

    public static float getFloatDiff(float v1, float v2, int scale) {
        return getFloatDiff(v1, v2, scale, RoundingMode.CEILING);
    }

    public static float getFloatDiff(float v1, float v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).floatValue();
    }

    public static double getDoubleDiff(double v1, double v2, int scale) {
        return getDoubleDiff(v1, v2, scale, RoundingMode.CEILING);
    }

    public static double getDoubleDiff(double v1, double v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).doubleValue();
    }

    public static double getFloatAdd(float v1, float v2, int scale) {
        return getFloatAdd(v1, v2, scale, RoundingMode.CEILING);
    }

    public static float getFloatAdd(float v1, float v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).floatValue();
    }

    public static double getDoubleAdd(double v1, double v2, int scale) {
        return getDoubleAdd(v1, v2, scale, RoundingMode.CEILING);
    }

    public static double getDoubleAdd(double v1, double v2, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2)).setScale(scale, roundingMode).doubleValue();
    }

    public static double getDouble(double v, int scale) {
        return getDouble(v, scale, RoundingMode.CEILING);
    }

    public static double getDouble(double v, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v).setScale(scale, roundingMode).doubleValue();
    }

    public static double getFloat(float v, int scale) {
        return getFloat(v, scale, RoundingMode.CEILING);
    }

    public static float getFloat(float v, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(v).setScale(scale, roundingMode).floatValue();
    }
}
