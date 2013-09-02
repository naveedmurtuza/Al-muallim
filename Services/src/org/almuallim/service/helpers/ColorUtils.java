package org.almuallim.service.helpers;

import java.awt.Color;

/**
 *
 * @author Naveed Quadri
 */
public class ColorUtils {

    public static String toRgbaFormat(Color c) {
        return toRgbaFormat(c, 1);
    }

    public static String toRgbaFormat(Color c, int alpha) {
        return String.format("rgba(%d,%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static String toRgbFormat(Color c) {
        return String.format("rgb(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static String toHexFormat(Color c) {
        String hexColour = Integer.toHexString(c.getRGB() & 0xffffff);
        if (hexColour.length() < 6) {
            hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
        }
        return "#" + hexColour;
    }

    public static Color fromHexFormat(String hexColor) {
        hexColor = hexColor.replace("#", "");
        return Color.decode(hexColor.contains("0x") ? hexColor : "0x" + hexColor);
    }
}
