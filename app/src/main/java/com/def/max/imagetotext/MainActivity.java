package com.def.max.imagetotext;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        PermissionUtil permissionUtil = new PermissionUtil(MainActivity.this);

        if (checkPermission(PermissionUtil.READ_CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,android.Manifest.permission.RECORD_AUDIO))
            {
                showPermissionExplanation(PermissionUtil.READ_CAMERA);
            }
            else if (permissionUtil.checkPermissionPreference(PermissionUtil.PERMISSION_CAMERA))
            {
                requestPermission(PermissionUtil.READ_CAMERA);
                permissionUtil.updatePermissionPreference(PermissionUtil.PERMISSION_CAMERA);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Please allow record audio permission in your app settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",MainActivity.this.getPackageName(),null);
                intent.setData(uri);
                this.startActivity(intent);
            }
        }

        AppCompatButton captureButton = findViewById(R.id.capture);

        captureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this,CameraActivity.class));

                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
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

        final AdView mAdView = findViewById(R.id.main_adView);
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

    private int checkPermission(int permission)
    {
        int status = PackageManager.PERMISSION_DENIED;

        switch (permission)
        {
            case PermissionUtil.READ_CAMERA:
                status = ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.CAMERA);
                break;
        }
        return status;
    }

    private void requestPermission(int permission)
    {
        switch (permission)
        {
            case PermissionUtil.READ_CAMERA:
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.CAMERA},PermissionUtil.REQUEST_CAMERA);
                break;
        }
    }

    private void showPermissionExplanation(final int permission)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        switch (permission)
        {
            case PermissionUtil.READ_CAMERA:
                builder.setMessage("This app need to access your camera..");
                builder.setTitle("Camera Permission Needed..");
                break;
        }

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (permission)
                {
                    case PermissionUtil.READ_CAMERA:
                        requestPermission(PermissionUtil.READ_CAMERA);
                        break;
                }
            }
        });

        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
