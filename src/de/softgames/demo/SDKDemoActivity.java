package de.softgames.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import de.softgames.sdk.SoftgamesActivity;


public class SDKDemoActivity extends Activity implements OnClickListener {

    private Button buttonRestarApp;
    private ImageButton sgButtonNoAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test notification - debug
        // SoftgamesUI.generateTestNotification(getApplicationContext());

        buttonRestarApp = (Button) findViewById(R.id.btn_restartApp);        
        buttonRestarApp.setOnClickListener(this);
        
        sgButtonNoAds =  (ImageButton) findViewById(R.id.sg_button_no_ads);
        sgButtonNoAds.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                // TODO Launch here set the purchase flow for the no ads product 
                Toast.makeText(getApplicationContext(), "Launch purchase flow", Toast.LENGTH_SHORT).show();
            }
        });
        
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
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_restartApp:
            Intent intent = new Intent(getApplicationContext(),
                    SoftgamesActivity.class);
            startActivity(intent);
            finish();
        default:
            break;
        }

    }

}
