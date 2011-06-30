package com.google.code.aosddl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import android.util.Log;

/**
 * ResourceBundle in Android is severly broken and does not work as advertised.
 * This class is used to find the correct resource file instead.
 * 
 * @author bpappin
 * 
 */
public class ConfigLoader {

	private static final String TAG = ConfigLoader.class.getSimpleName();

	public static Properties load(Locale locale) {
		Properties master = new Properties();

		// XXX We're depending on the list to maintain the least specific to
		// most specific order here.
		List<String> candidates = getCandidateNames("device", locale);
		for (int i = 0; i < candidates.size(); i++) {
			String fileName = candidates.get(i);
			try {
				InputStream in = ConfigLoader.class
						.getResourceAsStream(fileName);
				Properties p = new Properties();
				p.load(in);
				master.putAll(p);
			} catch (IOException e) {
				// ignored... likely not defined.
				// XXX Not sure I like depending on the exception.
				Log.d(TAG, "Device definition not found: " + fileName);
			}
		}

		return master;
	}

	public static List<String> getCandidateNames(String baseName, Locale locale) {
		// NOTE: This loads the list in the inverse order that the
		// ResourceBundle would from least specific to most specific.
		if (baseName == null) {
			throw new NullPointerException();
		}
		String manufacturer = locale.getLanguage();
		String device = locale.getCountry();
		String model = locale.getVariant();

		List<String> names = new ArrayList<String>(4);

		names.add(baseName);

		if (manufacturer.length() > 0) {
			names.add(baseName + "_" + manufacturer);
		}

		if (device.length() > 0 && manufacturer.length() > 0) {
			names.add(baseName + "_" + manufacturer + "_" + device);
		}

		if (model.length() > 0 && device.length() > 0
				&& manufacturer.length() > 0) {
			names.add(baseName + "_" + manufacturer + "_" + device + "_"
					+ model);
		}
		return names;
	}
}
