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

		((TextView) findViewById(R.id.buildBOARD)).setText("BOARD: "
				+ Build.BOARD);
		// Object codes = Build.VERSION_CODES;

		((TextView) findViewById(R.id.buildVERSION)).setText("VERSION: "
				+ "SDK " + Build.VERSION.SDK_INT + " "
				+ getSdkName(Build.VERSION.SDK_INT) + ", "
				+ Build.VERSION.CODENAME + " " + Build.VERSION.RELEASE);
		((TextView) findViewById(R.id.buildBRAND)).setText("BRAND: "
				+ Build.BRAND);
		((TextView) findViewById(R.id.buildDEVICE)).setText("DEVICE: "
				+ Build.DEVICE);
		((TextView) findViewById(R.id.buildDISPLAY)).setText("DISPLAY: "
				+ Build.DISPLAY);
		((TextView) findViewById(R.id.buildID)).setText("ID: " + Build.ID);
		((TextView) findViewById(R.id.buildMANUFACTURER))
				.setText("MANUFACTURER: " + Build.MANUFACTURER);
		((TextView) findViewById(R.id.buildMODEL)).setText("MODEL: "
				+ Build.MODEL);
		((TextView) findViewById(R.id.buildPRODUCT)).setText("PRODUCT: "
				+ Build.PRODUCT);
	}

	private String getSdkName(int sdkInt) {
		switch (sdkInt) {
		case 1:
			return "BASE";
		case 2:
			return "BASE_1_1";
		case 3:
			return "CUPCAKE";
		case 4:
			return "DONUT";
		case 5:
			return "ECLAIR";
		case 6:
			return "ECLAIR_0_1";
		case 7:
			return "ECLAIR_MR1";
		case 8:
			return "FROYO";
		case 9:
			return "GINGERBREAD";
		case 10:
			return "GINGERBREAD_MR1";
		case 11:
			return "HONEYCOMB";
		case 12:
			return "HONEYCOMB_MR1";
		case 10000:
			return "CUR_DEVELOPMENT";
		default:
			return "UNKNOWN";
		}
	}
}