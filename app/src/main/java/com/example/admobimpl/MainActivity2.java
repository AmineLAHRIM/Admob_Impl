package com.example.admobimpl;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.*;

public class MainActivity2 extends AppCompatActivity {

    private AdView mAdView;
    private LinearLayout container_banner;
    private Button btn_Interstitial;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setupBanner();
        setupInterstitial();

        btn_Interstitial=findViewById(R.id.btn_Interstitial);

        btn_Interstitial.setOnClickListener(v -> {
            showInterstitial();
        });
    }

    private void showInterstitial() {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }

    // called when you enter to MainActivity2 and exit TIME_COUNT times
    private void showInterstitialForCount() {
        final int TIME_COUNT=4;
        // initilize for 0
        Constant.TIME_BACKPRESS++;

        if(Constant.TIME_BACKPRESS>=TIME_COUNT){
            if(mInterstitialAd.isLoaded()){
                Constant.TIME_BACKPRESS=0;
                mInterstitialAd.show();
            }
        }

    }

    private void setupInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constant.INT);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }


    private void setupBanner() {
        container_banner = findViewById(R.id.container_banner);
        // if adview not exist not the correct layout

        mAdView = new AdView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container_banner.removeAllViews();
        container_banner.addView(mAdView, params);
        mAdView.setAdUnitId(Constant.BN);


        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);

        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .build();


        MobileAds.setRequestConfiguration(requestConfiguration);
        MobileAds.initialize(this, initializationStatus -> {

        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    public void onBackPressed() {
        showInterstitialForCount();
        super.onBackPressed();
    }
}