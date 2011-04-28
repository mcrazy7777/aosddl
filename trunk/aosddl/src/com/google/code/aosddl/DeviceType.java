package com.google.code.aosddl;

import android.os.Build;
import android.util.Log;

public enum DeviceType {
	MOTOROLA_ATRIX(1.6f), UNKNOWN(1f);

	private float textScaleMultiplier;

	private DeviceType(float textScaleMultiplier) {
		this.textScaleMultiplier = textScaleMultiplier;
	}

	public static DeviceType detect() {
		DeviceType detectedType = UNKNOWN;

		if (Build.MANUFACTURER.equals("motorola")
				&& Build.DEVICE.equals("olympus")
				&& Build.MODEL.equals("MB860")) {

			detectedType = MOTOROLA_ATRIX;
		}

		Log.i("DeviceType", "Detected DeviceType: " + detectedType.name());
		return detectedType;
	}

	public float getTextScaleMultiplier() {
		return textScaleMultiplier;
	}
}
