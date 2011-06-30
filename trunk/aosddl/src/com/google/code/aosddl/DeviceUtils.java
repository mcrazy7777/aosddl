package com.google.code.aosddl;

import android.util.Log;

public class DeviceUtils {

	// private final DeviceType deviceType;

	// public DeviceUtils() {
	// deviceType = DeviceType.detect();
	// }

	public int scaleTextSize(int size) {
		int newSize = size;

		if (!CapabilityLoader.getCapabilities().isUsingDefaultValues()) {
			newSize = (int) (CapabilityLoader.getCapabilities()
					.getTextScaleMultiplier() * size);
		}

		Log.d("DeviceUtils", "Font size was [" + size + "], scaling to ["
				+ newSize + "]");
		return newSize;
	}
}
