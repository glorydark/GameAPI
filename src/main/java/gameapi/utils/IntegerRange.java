package gameapi.utils;

import gameapi.tools.RandomTools;

import java.util.List;

/**
 * @author glorydark
 * @date {2023/6/22} {0:28}
 */
public class IntegerRange {

    public int min;

    public int max;

    public IntegerRange(int a, int b) {
        this.min = Math.min(a, b);
        this.max = Math.max(a, b);
    }

    public IntegerRange(String value, String splitIdentifier) {
        String[] strings = value.split(splitIdentifier);
        int i1 = Integer.parseInt(strings[0]);
        int i2 = Integer.parseInt(strings[1]);
        this.min = Math.min(i1, i2);
        this.max = Math.max(i1, i2);
    }

    public IntegerRange(List<Integer> values) {
        if (values == null) {
            this.min = 0;
            this.max = 0;
            return;
        }
        if (values.size() != 2) {
            this.min = 0;
            this.max = 0;
            return;
        }
        this.min = Math.min(values.get(0), values.get(1));
        this.max = Math.max(values.get(0), values.get(1));
    }

    public int getRandom() {
        if (this.min == this.max) {
            return this.max;
        }
        if (this.min > this.max) {
            return -1;
        }
        return RandomTools.getRandom(min, max);
    }

    public boolean isInvolved(int value) {
        return value >= min && value <= max;
    }

    @Override
    public String toString() {
        return "[" + this.min + "," + this.max + "]";
    }
}
