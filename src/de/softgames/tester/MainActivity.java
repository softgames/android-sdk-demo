package de.softgames.tester;

import static de.softgames.tester.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private AsyncTask<Void, Void, Void> _registerTask;
	private TelephonyManager telephonyManager;
	/**
	 * MNC: Mobile network code
	 * MCC: Mobile communication component 
	 */
	private TextView provider, mnc, mcc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Resources res = getResources();
		provider = (TextView) findViewById(R.id.provider_name_value);
		mcc = (TextView) findViewById(R.id.provider_mcc_value);
		mnc = (TextView) findViewById(R.id.provider_mnc_value);
		
		try {
			Log.d(TAG, "Trying to get mobile data connection info...");
			// Provider test
			telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String simOperator = telephonyManager.getSimOperatorName();
			if (simOperator != null && !simOperator.equals("")) {
				Log.d(TAG, "provider name: " + simOperator);
				provider.setText(simOperator);
				mcc.setText(simOperator.substring(0, 3));
				mnc.setText(simOperator.substring(3));
			} else {
				Log.d(TAG, "No mobile data found, get wifi info instead");			
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				//Service set identifier for a 802.11 network
				String wifiSSID = String.format(res.getString(R.string.wifi_connection), wifiInfo.getSSID());
				provider.setText(wifiSSID);
				mcc.setText(res.getString(R.string.not_applicable));
				mnc.setText(res.getString(R.string.not_applicable));	
			}
		} catch (Exception e) {
			Log.e(TAG, "Unknown error", e);		
		}		

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if (regId == null || regId.equals("")) {	
			Log.d(TAG, "regId is empty");
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);
		} else {
			Log.d(TAG, "registered with regId: " + regId);
			GCMRegistrar.setRegisteredOnServer(this, false);
			if (!GCMRegistrar.isRegisteredOnServer(this)) {
				final Context context = getApplicationContext();
				_registerTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						_registerTask = null;
					}

				};
				_registerTask.execute(null, null, null);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		if (_registerTask != null) {
			_registerTask.cancel(true);
		}
		try {
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e(TAG, "an error occurred destroying the Activity", e);
		}
		
		super.onDestroy();
	}

}
