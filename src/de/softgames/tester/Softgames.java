package de.softgames.tester;

import android.app.Application;
import de.softgames.sdk.util.SGSettings;


public class Softgames extends Application {

    @Override
    public void onCreate() {
        // Init the entry point activity o your app
        SGSettings.setLauncherActivity(MainActivity.class);
        super.onCreate();
    }


}
