package de.softgames.tester;

import static de.softgames.tester.CommonUtilities.SERVER_URL;
import static de.softgames.tester.CommonUtilities.STAG;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Helper class used to communicate with the server.
 */
public final class ServerUtilities {

	private static final int MAX_ATTEMPTS = 6;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	static boolean register(final Context context, final String regId) {
		Log.i(STAG, "registering device (regId = " + regId + ")");
		String serverUrl = SERVER_URL + "/" + context.getPackageName();
		Map<String, String> params = new HashMap<String, String>();

		String deviceId = Installation.id(context);

		params.put("device[device_id]", deviceId);
		params.put("device[registration_id]", regId);

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(STAG, "Attempt #" + i + " to register");
			try {
				// displayMessage(context,
				// context.getString(R.string.server_registering, i,
				// MAX_ATTEMPTS));
				post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				// String message =
				// context.getString(R.string.server_registered);
				// CommonUtilities.displayMessage(context, message);
				return true;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(STAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(STAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(STAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		// String message = context.getString(R.string.server_register_error,
		// MAX_ATTEMPTS);
		// CommonUtilities.displayMessage(context, message);
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String regId) {
		Log.i(STAG, "unregistering device (regId = " + regId + ")");
		
		String deviceId = Installation.id(context);
		String serverUrl = SERVER_URL + "/" + context.getPackageName() + "/" +  deviceId;

		try {
			delete(serverUrl, null);
			GCMRegistrar.setRegisteredOnServer(context, false);
			// String message = context.getString(R.string.server_unregistered);
			// CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			// String message =
			// context.getString(R.string.server_unregister_error,
			// e.getMessage());
			// CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */

	private static void delete(String endpoint, Map<String, String> params)
			throws IOException {
		request("DELETE", endpoint, params);
	}

	private static void post(String endpoint, Map<String, String> params)
			throws IOException {
		request("POST", endpoint, params);
	}

	private static void request(String method, String endpoint,
			Map<String, String> params) throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();

		byte[] bytes = null;
		if (params != null) {
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
			String body = bodyBuilder.toString();
			bytes = body.getBytes();
		}
		
		Log.v(STAG, "Send '" + method + "' to " + url);

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);

			if (params != null) {
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setFixedLengthStreamingMode(bytes.length);
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				// post the request
				OutputStream out = conn.getOutputStream();
				out.write(bytes);
				out.close();
			}
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

}
