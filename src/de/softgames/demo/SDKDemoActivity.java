package de.softgames.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.softgames.sdk.SoftgamesActivity;


public class SDKDemoActivity extends Activity implements OnClickListener {

    private Button buttonRestarApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test notification - debug
        // SoftgamesUI.generateTestNotification(getApplicationContext());

        buttonRestarApp = (Button) findViewById(R.id.btn_restartApp);        
        buttonRestarApp.setOnClickListener(this);
        
        
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
