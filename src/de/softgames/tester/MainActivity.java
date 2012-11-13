package de.softgames.tester;

import static de.softgames.tester.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {

	private AsyncTask<Void, Void, Void> _registerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Provider test
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TextView provider, mnc, mcc;
		provider = (TextView) findViewById(R.id.provider_name_value);
		provider.setText(telephonyManager.getSimOperatorName());

		mcc = (TextView) findViewById(R.id.provider_mcc_value);
		mcc.setText(telephonyManager.getSimOperator().substring(0, 3));

		mnc = (TextView) findViewById(R.id.provider_mnc_value);
		mnc.setText(telephonyManager.getSimOperator().substring(3));

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			if (!GCMRegistrar.isRegisteredOnServer(this)) {
				final Context context = this;
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
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

}
