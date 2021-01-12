package com.example.admobimpl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    // Layout Contain The Banner
    private LinearLayout container_banner;

    private Button btn_Interstitial;
    private Button btn_secondActivity;
    private Button btn_showreward;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBanner();
        setupInterstitial();
        setupRewardAds();

        btn_Interstitial=findViewById(R.id.btn_Interstitial);
        btn_secondActivity=findViewById(R.id.btn_secondActivity);
        btn_showreward=findViewById(R.id.btn_showreward);

        // CLICK TO SHOW INTERSTITIAL
        btn_Interstitial.setOnClickListener(v -> {
            showInterstitial();
        });

        // CLICK GO TO MAINACTIVITY2
        btn_secondActivity.setOnClickListener(v -> {
            Intent i=new Intent(this,MainActivity2.class);
            startActivity(i);
        });

        // CLICK BUTTON REWARD
        btn_showreward.setOnClickListener(v -> {
            if (rewardedAd.isLoaded()) {
                Activity activityContext = MainActivity.this;
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened() {
                        Log.d("rewardedAd", "rewardedAd: onRewardedAdOpened");
                        // Ad opened.
                        btn_showreward.setText("Loading..");

                    }

                    @Override
                    public void onRewardedAdClosed() {
                        // Ad closed.
                        Log.d("rewardedAd", "rewardedAd: onRewardedAdClosed");

                        setupRewardAds();
                    }

                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                        // User earned reward.
                        Log.d("rewardedAd", "rewardedAd: onUserEarnedReward");

                        btn_showreward.setText("YES!! You earn a point!!");
                    }

                    @Override
                    public void onRewardedAdFailedToShow(AdError adError) {
                        // Ad failed to display.

                    }
                };
                rewardedAd.show(activityContext, adCallback);
            } else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.");
            }
        });
    }

    private void setupRewardAds() {
        rewardedAd = new RewardedAd(this,
                Constant.RWD);

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                Log.d("rewardedAd", "rewardedAd: onRewardedAdLoaded");

                btn_showreward.setText("SHOW AD REWARD NOW!!");
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
                Log.d("rewardedAd", "rewardedAd: onRewardedAdFailedToLoad");

                btn_showreward.setText("Loading..");
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private void showInterstitial() {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
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
}