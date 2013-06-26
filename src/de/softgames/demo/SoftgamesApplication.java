package de.softgames.demo;


import android.app.Application;
import de.softgames.sdk.util.SGSettings;


/**
 * The Class Softgames used to initialize the SDK behaviour mainly.
 */
public class SoftgamesApplication extends Application {

    public static final String TAG = SoftgamesApplication.class.getSimpleName();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initializes the GoogleAnalytics tracker object
        SGSettings.initGAnalyticsTracker(getApplicationContext());

        /*
         * Init your app's entry point activity. This is the activity that you
         * want to be called when the app starts
         */
        SGSettings.setLauncherActivity(SDKDemoActivity.class);

        /*
         * This method sets the teaser image that is going to be
         * displayed in the cross-promotion page. This image is related to your
         * game
         */
        SGSettings.setTeaserImage(getResources().getDrawable(
                R.drawable.teaser_image));

        /*
         * Set the name of the game.
         */
        SGSettings.setGameName(getResources().getString(R.string.app_name));


    }


}
