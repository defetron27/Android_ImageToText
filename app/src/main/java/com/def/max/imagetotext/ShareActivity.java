package com.def.max.imagetotext;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class ShareActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Toolbar toolbar = findViewById(R.id.share_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String text = intent.getExtras().getString("text");

        AppCompatTextView textView = findViewById(R.id.capture_text);
        textView.setText(text);

        AppCompatButton shareBtn = findViewById(R.id.share);

        final AppCompatEditText textSubject = findViewById(R.id.text_subject);

        shareBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String subject = textSubject.getText().toString();

                if (subject.length() == 0)
                {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,text);
                    startActivity(Intent.createChooser(shareIntent,"Share Text"));
                }
                else
                {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,text);
                    startActivity(Intent.createChooser(shareIntent,"Share Text"));
                }
            }
        });

        MobileAds.initialize(this, "ca-app-pub-4443035718642364~2425994198");
        final InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4443035718642364/2106429990");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.show();
                }
                else
                {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

                handler.postDelayed(this, 300 * 1000);
            }
        }, 3000);

        final AdView mAdView = findViewById(R.id.share_adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        final Handler handlerBanner = new Handler();
        handlerBanner.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                AdRequest adRequest2 = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest2);

                handlerBanner.postDelayed(this, 300 * 1000);
            }
        }, 1000);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }
}
