package fr.ydelouis.selfoss.util;

import android.graphics.Color;

public class ColorUtil {

	public static int parseColor(String colorString) {
		try {
			return Color.parseColor(colorString);
		} catch (IllegalArgumentException e) {
			if (isShortColorCode(colorString)) {
				return parseShortColorCode(colorString);
			} else {
				return Color.BLACK;
			}
		}
	}

	private static boolean isShortColorCode(String colorString) {
		return colorString.length() == 4 && colorString.startsWith("#") && isHexadecimalColor(colorString);
	}

	private static boolean isHexadecimalColor(String colorString) {
		try {
			Long.parseLong(colorString.substring(1), 16);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static int parseShortColorCode(String colorString) {
		String rgb = colorString.substring(1);
		int r = Integer.parseInt(rgb.substring(0, 1)+rgb.substring(0, 1), 16);
		int g = Integer.parseInt(rgb.substring(1, 2)+rgb.substring(1, 2), 16);
		int b = Integer.parseInt(rgb.substring(2, 3)+rgb.substring(2, 3), 16);
		return Color.rgb(r, g, b);
	}
}
