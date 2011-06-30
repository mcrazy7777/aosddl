package com.google.code.aosddl;

import android.util.Log;

public class DeviceUtils {

	private final DeviceType deviceType;

	public DeviceUtils() {
		deviceType = DeviceType.detect();
	}

	public int scaleTextSize(int size) {
		int newSize = size;

		if (deviceType != DeviceType.UNKNOWN) {
			newSize = (int) (deviceType.getTextScaleMultiplier() * size);
		}

		Log.d("DeviceUtils", "Font size was [" + size + "], scaling to ["
				+ newSize + "]");
		return newSize;
	}
}
