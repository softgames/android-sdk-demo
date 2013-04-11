package de.softgames.demo;

import android.app.Application;
import android.graphics.drawable.Drawable;
import de.softgames.demo.R;
import de.softgames.sdk.util.SGSettings;


/**
 * The Class Softgames used to initialize the SDK behaviour mainly.
 */
public class Softgames extends Application {
    
    //Assign the proper value to the constant teaserImgId, you should provide this image
    private final int teaserImgId = R.drawable.teaser_image;
    private Drawable teaserImg;

    @Override
    public void onCreate() {
        /*
         * Init your app's entry point activity. This is the activity that you
         * want to be called when the app starts
         */
        SGSettings.setLauncherActivity(SDKDemoActivity.class);

        /*
         * In case your app does not require an active internet connection,
         * please set this VAR as false
         */
        // SGSettings.setInternetRequired(false);
        
        /*
         * You can set with this method the teaser image that is going to be
         * displayed in the cross-promotion page. This image is relate to your game
         */
        teaserImg = getResources().getDrawable(teaserImgId);
        SGSettings.setTeaserImage(teaserImg);
        
        /*
         * Set the name of the game.
         * */
        SGSettings.setGameName(getResources().getString(R.string.app_name));
        super.onCreate();
    }


}
