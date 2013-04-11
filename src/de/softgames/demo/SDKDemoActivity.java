package de.softgames.demo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.softgames.demo.R;
import de.softgames.sdk.SGRegistrator;
import de.softgames.sdk.SoftgamesActivity;


public class SDKDemoActivity extends Activity {

    private static final String TAG = "MainActivity";
    private TelephonyManager telephonyManager;
    /* MNC: Mobile network code MCC: Mobile communication component */
    private TextView provider, mnc, mcc;
    // Registrator object used to establish a communication with Google Cloud
    // messaging
    public SGRegistrator registrator;

    private Button buttonRestarApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Resources res = getResources();

        // Test notification - debug
        // SoftgamesUI.generateTestNotification(getApplicationContext());

        /*
         * You must instantiate this object in order to get working the push
         * notifications.
         */
        registrator = new SGRegistrator(this);

        provider = (TextView) findViewById(R.id.provider_name_value);
        mcc = (TextView) findViewById(R.id.provider_mcc_value);
        mnc = (TextView) findViewById(R.id.provider_mnc_value);

        buttonRestarApp = (Button) findViewById(R.id.btn_restartApp);
        buttonRestarApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        SoftgamesActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
                // Service set identifier for a 802.11 network
                String wifiSSID = String.format(
                        res.getString(R.string.wifi_connection),
                        wifiInfo.getSSID());
                provider.setText(wifiSSID);
                mcc.setText(res.getString(R.string.not_applicable));
                mnc.setText(res.getString(R.string.not_applicable));
            }
        } catch (Exception e) {
            Log.e(TAG, "Unknown error", e);
        }

        /*
         * this method must be invoked in order to register the device on the
         * softgames server
         */
        registrator.registerMe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_exit:
            android.os.Process.killProcess(android.os.Process.myPid());
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // This method is called to make sure that the async task initiated by
        // the registrator is terminated
        registrator.killTask();
        super.onDestroy();
    }

}
