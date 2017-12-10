package Image;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ColorHelper {

    private final static int LOW = 0;
    private final static int HIGH = 255;
    private final static int HALF = (HIGH + 1) / 2;

    private final static Map<Integer, Color> map = initNumberToColorMap();
    private static int factor;

    public static Color numberToColor(final double value) {
        return numberToColorPercentage(value / 100);
    }

    public static Color numberToColorPercentage(final double value) {
        Double d = value * factor;
        int index = d.intValue();
        if (index == factor) {
            index--;
        }
        return map.get(index);
    }

    private static Map<Integer, Color> initNumberToColorMap() {
        HashMap<Integer, Color> localMap = new HashMap<>();
        int r = LOW;
        int g = LOW;
        int b = HALF;

        int redFactor = 0;
        int greenFactor = 0;
        int blueFactor = 1;

        int count = 0;
        while (true) {
            localMap.put(count++, new Color(r, g, b));
            if (b == HIGH) {
                greenFactor = 1;
            }
            if (g == HIGH) {
                blueFactor = -1;
            }
            if (b == LOW) {
                redFactor = +1;
            }
            if (r == HIGH) {
                greenFactor = -1;
            }
            if (g == LOW && b == LOW) {
                redFactor = -1;
            }
            if (r < HALF && g == LOW && b == LOW) {
                break;
            }
            r += redFactor;
            g += greenFactor;
            b += blueFactor;

            r = rangeCheck(r);
            g = rangeCheck(g);
            b = rangeCheck(b);
        }

        List<Integer> list = new ArrayList<>(localMap.keySet());
        Collections.sort(list);
        Integer max = list.get(list.size() - 1);
        factor = max + 1;

        return localMap;
    }

    private static int rangeCheck(final int value) {
        if (value > HIGH) {
            return HIGH;
        } else if (value < LOW) {
            return LOW;
        }
        return value;
    }
}