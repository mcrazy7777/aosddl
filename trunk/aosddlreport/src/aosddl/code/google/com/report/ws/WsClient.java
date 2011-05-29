package aosddl.code.google.com.report.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.util.Log;

public class WsClient {
	private static final String TAG = "WsClient";

	// XXX Emulator localhost alias (10.0.2.2).
	// public static final String BASE_URL = "http://10.0.2.2:8081";
	public static final String BASE_URL = "http://moap.sixgreen.com/ws";

	public void capture(final Activity activity, final String email,
			final String reference, final String tags,
			final Map<String, String> data, final WsStatusListener listener) {

		Log.d(TAG, "###### capture: email=" + email + ", reference="
				+ reference + ", tags=" + tags);

		// Runnable exec = new Runnable() {
		//
		// @Override
		// public void run() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		Set<String> keySet = data.keySet();
		for (String key : keySet) {
			params.add(new BasicNameValuePair(key, data.get(key)));
		}

		if (email != null && email.length() > 0) {
			params.add(new BasicNameValuePair("email", email));
		}
		if (reference != null && reference.length() > 0) {
			params.add(new BasicNameValuePair("reference", reference));
		}
		if (tags != null && tags.length() > 0) {
			params.add(new BasicNameValuePair("tags", tags));
		}

		final String result = httpPost(BASE_URL + "/ddl/capture", params);
		//
		// Runnable returnResult = new Runnable() {
		// @Override
		// public void run() {
		Log.d("RESPONSE [capture]", (result != null) ? result
				: "[null response]");
		if (listener != null) {
			if (result != null && "ok".equalsIgnoreCase(result.trim())) {
				listener.wsStatus(true, "Device properties captured");
			} else {
				listener.wsStatus(false, result);
			}
		}
		// }
		// };
		//
		// activity.runOnUiThread(returnResult);
		// }
		// };
		//
		// Thread thread = new Thread(exec);
		// thread.start();

	}

	private String httpPost(String url, List<NameValuePair> params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {

			httppost.setEntity(new UrlEncodedFormEntity(params));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.i(TAG, "httpPost Status:["
					+ response.getStatusLine().toString() + "]");
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				// Log.i("RESPONSE", "Result of converstion: [" + result + "]");
				Log.d("RESPONSE [httpPost]", (result != null) ? result
						: "[null response]");

				instream.close();
				return result;
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG, "There was a protocol based error", e);
		} catch (IOException e) {
			Log.e(TAG, "There was an IO Stream related error", e);
		}
		return null;
	}

	public String httpGet(String url, HttpParams params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		if (params != null) {
			httpget.setParams(params);
		}
		// HttpResponse response;

		try {
			HttpResponse response = httpclient.execute(httpget);
			Log.i(TAG, "Status:[" + response.getStatusLine().toString() + "]");
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				Log.d("RESPONSE [httpGet]", (result != null) ? result
						: "[null response]");

				instream.close();
				return result;
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "There was a protocol based error", e);
		} catch (HttpHostConnectException e) {
			Log.e(TAG, "HTTP Connect error", e);
		} catch (IOException e) {
			Log.e(TAG, "There was an IO Stream related error", e);
		}

		return null;
	}

	/**
	 * To convert the InputStream to String we use the BufferedReader.readLine()
	 * method. We iterate until the BufferedReader return null which means
	 * there's no more data to read. Each line will appended to a StringBuilder
	 * and returned as String.
	 */
	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder buffer = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

}
