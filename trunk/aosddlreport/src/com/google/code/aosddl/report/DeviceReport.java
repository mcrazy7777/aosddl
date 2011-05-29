package com.google.code.aosddl.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.aosddl.report.ws.WsClient;
import com.google.code.aosddl.report.ws.WsStatusListener;

public class DeviceReport extends ListActivity {
	private static final String TAG = DeviceReport.class.getSimpleName();

	// XXX Made these string so they don't change across releases.
	public static final String EXTRA_EMAIL = "com.google.code.aosddl.report.EMAIL";
	public static final String EXTRA_REFERENCE = "com.google.code.aosddl.report.REFERENCE";
	public static final String EXTRA_TAGS = "com.google.code.aosddl.report.TAGS";

	// XXX We keep these here because some of them don't exist in older android
	// versions. They should match the constants in Configuration -JFBP
	private static final int SCREENLAYOUT_SIZE_UNDEFINED = 0x00;
	private static final int SCREENLAYOUT_SIZE_SMALL = 0x01;
	private static final int SCREENLAYOUT_SIZE_NORMAL = 0x02;
	private static final int SCREENLAYOUT_SIZE_LARGE = 0x03;
	private static final int SCREENLAYOUT_SIZE_XLARGE = 0x04;

	private EditText emailEditText;
	private EditText referenceEditText;
	private EditText tagsEditText;
	// private ListView paramListView;
	private Button cancelButton;
	private Button submitButton;

	private final Map<String, String> params = new HashMap<String, String>();
	private List<Entry<String, String>> paramEntries = new ArrayList<Entry<String, String>>();
	private ParamMapListAdapter adapter;
	private Handler handler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		emailEditText = (EditText) findViewById(R.id.emailEditText);
		referenceEditText = (EditText) findViewById(R.id.referenceEditText);
		tagsEditText = (EditText) findViewById(R.id.tagsEditText);
		// paramListView = (ListView) findViewById(R.id.paramListView);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		submitButton = (Button) findViewById(R.id.submitButton);

		updateInputs();

		adapter = new ParamMapListAdapter(this, paramEntries);
		setListAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		loadParams();
	}

	public void submitButtonClicked(View view) {
		final ProgressDialog progress = ProgressDialog.show(DeviceReport.this,
				"", "Saving. Please wait...", true);
		progress.show();
		Runnable submitExec = new Runnable() {
			@Override
			public void run() {

				try {
					final String emailValue = emailEditText.getText()
							.toString();
					final String referenceValue = referenceEditText.getText()
							.toString();
					final String tagsValue = tagsEditText.getText().toString();
					WsClient client = new WsClient();
					client.capture(DeviceReport.this, emailValue,
							referenceValue, tagsValue, params,
							new WsStatusListener() {
								@Override
								public void wsStatus(final boolean success,
										final String message) {
									Log.d(TAG, "###### success[" + success
											+ "] " + message);
									progress.dismiss();
									handler.post(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(DeviceReport.this,
													message, Toast.LENGTH_LONG)
													.show();
											if (success) {
												setResult(RESULT_OK);
												finish();
											}
										}
									});

								}
							});
				} catch (Exception e) {
					// FIXME we really want to catch things like
					// UnknownhostException
					// but we need to modify the client code first.
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				} finally {
					progress.dismiss();
				}
			}
		};

		Thread thread = new Thread(submitExec);
		thread.start();

		// handler.post(submitExec);

	}

	public void cancelButtonClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void loadParams() {
		Runnable viewOrders = new Runnable() {
			@Override
			public void run() {
				updateParams();
			}
		};
		Thread thread = new Thread(viewOrders);
		thread.start();
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (params != null && params.size() > 0) {
				adapter.notifyDataSetChanged();

				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					adapter.add(entry);
				}

			}
			// m_ProgressDialog.dismiss();
			adapter.notifyDataSetChanged();
		}
	};

	private void updateInputs() {

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String extraEmail = bundle.getString(EXTRA_EMAIL);
			String extraRef = bundle.getString(EXTRA_REFERENCE);
			String extraTags = bundle.getString(EXTRA_TAGS);

			if (extraEmail != null) {
				emailEditText.setText(extraEmail);
			} else {
				String packageName = getPackageName();
				PackageManager packageMan = getPackageManager();
				if (packageMan.checkPermission(
						"android.permission.GET_ACCOUNTS", packageName) == PackageManager.PERMISSION_GRANTED) {
					AccountManager accountManager = AccountManager.get(this);
					Account[] accounts = accountManager
							.getAccountsByType("com.google");
					if (accounts.length > 0) {
						emailEditText.setText(accounts[0].name);
						// params.put("email", accounts[0].name);
					}
				}
			}

			if (extraRef != null) {
				referenceEditText.setText(extraRef);
			}

			if (extraTags != null) {
				tagsEditText.setText(extraTags);
			}
		}

	}

	private void updateParams() {
		// @FormParam("email") String email,
		// @FormParam("reference") String reference,
		// @FormParam("tags") String tags,

		// // Don't update the email if it's already set.
		// if (!params.containsKey("email") || params.get("email").length() ==
		// 0) {
		// String packageName = getPackageName();
		// PackageManager packageMan = getPackageManager();
		// if (packageMan.checkPermission("android.permission.GET_ACCOUNTS",
		// packageName) == PackageManager.PERMISSION_GRANTED) {
		// AccountManager accountManager = AccountManager.get(this);
		// Account[] accounts = accountManager
		// .getAccountsByType("com.google");
		// if (accounts.length > 0) {
		// params.put("email", accounts[0].name);
		// }
		// }
		// }
		//
		// // FIXME we actually want to fill the reference and tags from values
		// // passed to our activity... this will do until we have that
		// // implemented.
		// if (!params.containsKey("reference")
		// || params.get("reference").length() == 0) {
		// params.put("reference", referenceEditText.getText().toString());
		// }
		// if (!params.containsKey("tags") || params.get("tags").length() == 0)
		// {
		// params.put("tags", tagsEditText.getText().toString());
		// }

		params.put("buildVersionCodename", DDLBuild.VERSION.CODENAME);
		params.put("buildVersionIncremental", DDLBuild.VERSION.INCREMENTAL);
		params.put("buildVersionRelease", DDLBuild.VERSION.RELEASE);
		params.put("buildVersionSdkInt",
				Integer.toString(DDLBuild.VERSION.SDK_INT));
		params.put("buildBoard", DDLBuild.BOARD);
		params.put("buildBootloader", DDLBuild.BOOTLOADER);
		params.put("buildBrand", DDLBuild.BRAND);
		params.put("buildCpuAbi", DDLBuild.CPU_ABI);
		params.put("buildCpuApi2", DDLBuild.CPU_ABI2);
		params.put("buildDevice", DDLBuild.DEVICE);
		params.put("buildDisplay", DDLBuild.DISPLAY);
		params.put("buildFingerprint", DDLBuild.FINGERPRINT);
		params.put("buildHardware", DDLBuild.HARDWARE);
		params.put("buildHost", DDLBuild.HOST);
		params.put("buildId", DDLBuild.ID);
		params.put("buildManufacturer", DDLBuild.MANUFACTURER);
		params.put("buildModel", DDLBuild.MODEL);
		params.put("buildProduct", DDLBuild.PRODUCT);
		params.put("buildRadio", DDLBuild.RADIO);
		// Build.SERIAL Not included in SDK 8
		params.put("buildSerial", DDLBuild.SERIAL);
		params.put("buildTags", DDLBuild.TAGS);
		params.put("buildTime", Long.toString(DDLBuild.TIME));
		params.put("buildType", DDLBuild.TYPE);
		params.put("buildUser", DDLBuild.USER);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		params.put("screenDensityLogical", Float.toString(metrics.density));
		params.put("screenDensityScaled", Float.toString(metrics.scaledDensity));
		params.put("screenDensityDpi", Integer.toString(metrics.densityDpi));
		params.put("screenHeightPixles", Integer.toString(metrics.heightPixels));
		params.put("screenWidthPixles", Integer.toString(metrics.widthPixels));
		params.put("screenXDpi", Float.toString(metrics.xdpi));
		params.put("screenYDpi", Float.toString(metrics.ydpi));

		Configuration config = getResources().getConfiguration();

		params.put("configKeyboard", getKeyboardEnumFromValue(config.keyboard));
		params.put("configLayoutLong", getScreenLayoutEnumFromValue(config));
		params.put("configLayoutSize", getScreenLayoutSizeEnumFromValue(config));
		params.put("configMcc", Integer.toString(config.mcc));
		params.put("configMnc", Integer.toString(config.mnc));
		params.put("configLocale", config.locale.toString());

		// adapter.notifyDataSetChanged();
		// paramListView.setAdapter(adapter);
		// adapter.notifyDataSetInvalidated();
		runOnUiThread(returnRes);

	}

	private String getScreenLayoutSizeEnumFromValue(Configuration config) {

		int screenLayout = config.screenLayout;
		Log.d(TAG, "###### screenLayout=" + screenLayout);
		int screenSize = screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		Log.d(TAG, "###### screenSize=" + screenSize);
		switch (screenSize) {
		case SCREENLAYOUT_SIZE_UNDEFINED:
			return DDLLayout.UNDEFINED.name();
		case SCREENLAYOUT_SIZE_SMALL:
			return DDLLayout.SIZE_SMALL.name();
		case SCREENLAYOUT_SIZE_NORMAL:
			return DDLLayout.SIZE_NORMAL.name();
		case SCREENLAYOUT_SIZE_LARGE:
			return DDLLayout.SIZE_LARGE.name();
		case SCREENLAYOUT_SIZE_XLARGE:
			return DDLLayout.SIZE_XLARGE.name();
		default:
			return DDLLayout.CORRUPTED.name();
		}

	}

	private String getScreenLayoutEnumFromValue(Configuration config) {

		int screenLayout = config.screenLayout;
		Log.d(TAG, "###### screenLayout=" + screenLayout);
		int screenFlow = screenLayout & Configuration.SCREENLAYOUT_LONG_MASK;
		Log.d(TAG, "###### screenFlow=" + screenFlow);

		switch (screenFlow) {
		case Configuration.SCREENLAYOUT_LONG_UNDEFINED:
			return DDLLayout.UNDEFINED.name();
		case Configuration.SCREENLAYOUT_LONG_YES:
			return DDLLayout.LONG_YES.name();
		case Configuration.SCREENLAYOUT_LONG_NO:
			return DDLLayout.LONG_NO.name();
		default:
			return DDLLayout.CORRUPTED.name();
		}

		// if ((config & Configuration.SCREENLAYOUT_LONG_YES) ==
		// Configuration.SCREENLAYOUT_LONG_YES) {
		// return DDLLayout.LONG_YES.name();
		// }
		// return DDLLayout.LONG_NO.name();
	}

	private String getKeyboardEnumFromValue(int keyboard) {
		switch (keyboard) {
		case Configuration.KEYBOARD_NOKEYS:
			return DDLKeyboard.NOKEYS.name();
		case Configuration.KEYBOARD_12KEY:
			return DDLKeyboard.KEY12.name();
		case Configuration.KEYBOARD_QWERTY:
			return DDLKeyboard.QWERTY.name();
		case Configuration.KEYBOARD_UNDEFINED:
			return DDLKeyboard.UNDEFINED.name();
		default:
			// XXX This is the unknown case.
			return DDLKeyboard.CORRUPTED.name();
		}
	}

	class ParamMapListAdapter extends ArrayAdapter<Entry<String, String>> {
		// private Map<String, String> items;

		public ParamMapListAdapter(Context context,
				List<Entry<String, String>> items) {
			super(context, android.R.layout.simple_list_item_1, items);
			// this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(android.R.layout.simple_list_item_1, null);
			}

			Entry<String, String> entry = getItem(position);
			// Entry<String, String> o = items.get(position);
			if (entry != null) {
				TextView tt = (TextView) v.findViewById(android.R.id.text1);
				if (tt != null) {
					tt.setText(entry.getKey() + "=" + entry.getValue());
				}

			}
			return v;
		}
	}
}