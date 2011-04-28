package com.google.code.aosddl.info;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class DeviceInfoActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		TextView lblResolution = (TextView) findViewById(R.id.lblResolution);
		lblResolution.setText(String.format("%d x %d", dm.widthPixels,
				dm.heightPixels));

		TextView lblDensity = (TextView) findViewById(R.id.lblDensity);
		lblDensity.setText(String.format(
				"xdpi:%.2f\nydpi:%.2f\ndensityDpi:%d\ndensity:%.2f", dm.xdpi,
				dm.ydpi, dm.densityDpi, dm.density));

		StringBuilder deviceInfo = new StringBuilder();
		deviceInfo.append("OS Version: ")
				.append(System.getProperty("os.version")).append(" (")
				.append(Build.VERSION.INCREMENTAL).append(")");
		deviceInfo.append("\nOS API Level: ").append(Build.VERSION.SDK);
		deviceInfo.append("\nBrand: ").append(Build.BRAND);
		deviceInfo.append("\nManufacturer: ").append(Build.MANUFACTURER);
		deviceInfo.append("\nDevice: ").append(Build.DEVICE);
		deviceInfo.append("\nModel (Product): ").append(Build.MODEL)
				.append(" (").append(Build.PRODUCT).append(")");

		TextView lblDeviceInfo = (TextView) findViewById(R.id.lblDeviceInfo);
		lblDeviceInfo.setText(deviceInfo.toString());
	}
}