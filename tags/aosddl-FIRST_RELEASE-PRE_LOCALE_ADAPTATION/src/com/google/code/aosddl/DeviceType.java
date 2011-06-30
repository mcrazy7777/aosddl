package com.google.code.aosddl;

import android.os.Build;
import android.util.Log;

public enum DeviceType {
	MOTOROLA_ATRIX(1.6f), LG_GX2(1.6f), UNKNOWN(1f);

	private float textScaleMultiplier;

	private DeviceType(float textScaleMultiplier) {
		this.textScaleMultiplier = textScaleMultiplier;
	}

	public static DeviceType detect() {
		DeviceType detectedType = UNKNOWN;

		if (isMotorolaAtrix()) {
			detectedType = MOTOROLA_ATRIX;
		} else if (isLgGx2()) {
			detectedType = LG_GX2;
		}

		Log.i("DeviceType", "Detected DeviceType: " + detectedType.name());
		return detectedType;
	}

	public float getTextScaleMultiplier() {
		return textScaleMultiplier;
	}

	private static boolean isLgGx2() {
		return Build.MANUFACTURER.equals("lge") && Build.DEVICE.equals("p999")
				&& Build.MODEL.equals("LG-P999");
	}

	private static boolean isMotorolaAtrix() {
		return Build.MANUFACTURER.equals("motorola")
				&& Build.DEVICE.equals("olympus")
				&& Build.MODEL.equals("MB860");
	}
}
