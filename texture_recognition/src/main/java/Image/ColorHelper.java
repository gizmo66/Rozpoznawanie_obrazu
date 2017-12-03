package Image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // factor (increment or decrement)
        int rF = 0;
        int gF = 0;
        int bF = 1;

        int count = 0;
        // 1276 steps
        while (true) {
            localMap.put(count++, new Color(r, g, b));
            if (b == HIGH) {
                gF = 1; // increment green
            }
            if (g == HIGH) {
                bF = -1; // decrement blue
                // rF = +1; // increment red
            }
            if (b == LOW) {
                rF = +1; // increment red
            }
            if (r == HIGH) {
                gF = -1; // decrement green
            }
            if (g == LOW && b == LOW) {
                rF = -1; // decrement red
            }
            if (r < HALF && g == LOW && b == LOW) {
                break; // finish
            }
            r += rF;
            g += gF;
            b += bF;
            r = rangeCheck(r);
            g = rangeCheck(g);
            b = rangeCheck(b);
        }
        initList(localMap);
        return localMap;
    }

    private static void initList(final HashMap<Integer, Color> localMap) {
        List<Integer> list = new ArrayList<>(localMap.keySet());
        Collections.sort(list);
        Integer min = list.get(0);
        Integer max = list.get(list.size() - 1);
        factor = max + 1;
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