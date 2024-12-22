package gameapi.tools;

/**
 * @author glorydark
 */
public class CalculatorTools {

    public static boolean isBetween(int value, int v1, int v2) {
        return value >= Math.min(v1, v2) && value <= Math.max(v1, v2);
    }

    public static boolean isBetween(double value, double v1, double v2) {
        return value >= Math.min(v1, v2) && value <= Math.max(v1, v2);
    }
}