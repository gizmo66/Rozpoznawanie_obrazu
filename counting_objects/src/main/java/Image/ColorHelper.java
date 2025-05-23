package Image;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ColorHelper {

    private final static int LOW = 0;
    private final static int HIGH = 255;
    private final static int HALF = (HIGH + 1) / 2;

    private static int r1;
    private static int g1;
    private static int b1;
    private static int r2;
    private static int g2;
    private static int b2;

    private final static Map<Integer, Color> map = initNumberToColorMap();
    private static int factor;

    public static Color numberToColor(final double value) {
        return numberToColorPercentage(value / 100);
    }

    private static Color numberToColorPercentage(final double value) {
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

    static boolean isSimilar(int color1, int color2, double maxDistance) {
        return getDistance(color1, color2) < maxDistance;
    }

    public static int getDistance(int color1, int color2) {
        r1 = (color1 >> 16) & 0xFF;
        g1 = (color1 >> 8) & 0xFF;
        b1 = (color1) & 0xFF;

        r2 = (color2 >> 16) & 0xFF;
        g2 = (color2 >> 8) & 0xFF;
        b2 = (color2) & 0xFF;

        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

}