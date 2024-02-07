package gameapi.utils;

import gameapi.tools.RandomTools;

import java.util.List;

/**
 * @author glorydark
 * @date {2023/6/22} {0:28}
 */
public class DoubleRange {

    public double min;

    public double max;

    public DoubleRange(double a, double b) {
        this.min = Math.min(a, b);
        this.max = Math.max(a, b);
    }

    public DoubleRange(List<Double> values) {
        if (values == null) {
            this.min = 0d;
            this.max = 0d;
            return;
        }
        if (values.size() != 2) {
            this.min = 0d;
            this.max = 0d;
            return;
        }
        this.min = Math.min(values.get(0), values.get(1));
        this.max = Math.max(values.get(0), values.get(1));
    }

    public double getRandom() {
        if (this.min == this.max) {
            return this.max;
        }
        if (this.min > this.max) {
            return -1;
        }
        return RandomTools.getRandom(min, max);
    }

    public boolean isInvolved(double value) {
        return value >= min && value <= max;
    }

    @Override
    public String toString() {
        return "[" + this.min + "," + this.max + "]";
    }
}
