package de.softgames.tester;

import android.app.Application;
import de.softgames.sdk.util.SGSettings;


/**
 * The Class Softgames used to initialize the SDK behaviour mainly.
 */
public class Softgames extends Application {

    @Override
    public void onCreate() {
        /*
         * Init your app's entry point activity. This is the activity that you
         * want to be called when the app starts
         */
        SGSettings.setLauncherActivity(MainActivity.class);

        /* The game's name must be set in order to track the ads */
        SGSettings.setGameName("MyAwesomeGame");
        /*
         * In case your app does not require an active internet connection,
         * please set this VAR as false
         */
        // SGSettings.setInternetRequired(false);
        super.onCreate();
    }


}
