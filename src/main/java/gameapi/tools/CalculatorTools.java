package gameapi.tools;

/**
 * @author glorydark
 */
public class CalculatorTools {

    public static boolean isBetween(int value, int min, int max) {
        return value >= min || value <= max;
    }

    public static boolean isBetween(double value, double min, double max) {
        return value >= min || value <= max;
    }
}