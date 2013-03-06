package de.softgames.demo_sdk.test;


import android.test.ActivityInstrumentationTestCase2;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import de.softgames.sdk.OpenxAdView;
import de.softgames.sdk.SoftgamesIntro;
import de.softgames.sdk.util.SGSettings;


public class SoftgamesTestSuite extends
        ActivityInstrumentationTestCase2<SoftgamesIntro> {

    public static final int OPENX_ZONE_ID = 2;

    public static final String SPLASH_SCREEN_ID = "splashScreen";

    public static final String DELIVERY_URL = "87.230.102.59:82/openx/www/delivery";

    public SoftgamesIntro softgamesIntro;

    public ViewFlipper flipper;

    public LinearLayout splashScreen;

    public LinearLayout adsLayout;

    public OpenxAdView adView;

    public WebView openxWebView;



    @SuppressWarnings("deprecation")
    public SoftgamesTestSuite() {
        super("de.softgames.sdk", SoftgamesIntro.class);
    }

    public SoftgamesTestSuite(Class<SoftgamesIntro> activityClass) {
        super(activityClass);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        softgamesIntro = getActivity();

        flipper = (ViewFlipper) softgamesIntro
                .findViewById(de.softgames.sdk.R.id.softgames_master);

        splashScreen = (LinearLayout) softgamesIntro
                .findViewById(de.softgames.sdk.R.id.splashScreen);

        adsLayout = (LinearLayout) softgamesIntro
                .findViewById(de.softgames.sdk.R.id.adsLayout);

        adView = (OpenxAdView) softgamesIntro
                .findViewById(de.softgames.sdk.R.id.adview);

    }

    public void testPreconditions() {

        assertNotNull("The launcher activity can't be null!",
                SGSettings.getLauncherActivity());

        assertTrue("A app name must be provided", !SGSettings.getGameName()
                .equals(""));

    }

    public void testIntroLayout() {

        // FIXME make sure the layout order is correct
        assertTrue("The first child in the FlipView should be "
                + SPLASH_SCREEN_ID, flipper.getChildAt(0) == splashScreen);
    }

    public void testOpenxAds() {

        assertTrue(adView != null);

        assertEquals(adView.getDeliveryURL(), DELIVERY_URL);

        assertEquals(adView.getZoneID().intValue(), OPENX_ZONE_ID);
        
        openxWebView = (WebView) adView.findViewWithTag("openxWebView");

        assertNotNull(openxWebView);

    }


    /**
     * Test the registration on the Google cloud messaging server
     */
    public void testRegistration() {
        // TODO determine whether the creation of mock objects is necessary or
        // not
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}
