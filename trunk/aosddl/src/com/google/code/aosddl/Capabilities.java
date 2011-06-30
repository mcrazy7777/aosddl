package com.google.code.aosddl;

public interface Capabilities {
	public boolean isUsingDefaultValues();

	public Float getTextScaleMultiplier();

	/*
	 * Since Android 2.3 (“Gingerbread”) this is available via
	 * android.os.Build.SERIAL. Devices without telephony are required to report
	 * a unique device ID here; some phones may do so also.
	 */
	public boolean hasValidBuildSerial();

	public boolean isTablet();
}
