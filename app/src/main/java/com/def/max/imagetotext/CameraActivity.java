package com.def.max.imagetotext;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity
{
    private static final String TAG = CameraActivity.class.getSimpleName();

    private SurfaceView mCameraView;
    private CameraSource mCameraSource;
    private AppCompatTextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.recognition_text);

        AppCompatImageView sendOk = findViewById(R.id.ok_btn);

        sendOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = mTextView.getText().toString();

                if (text.length() == 0)
                {
                    Toast.makeText(CameraActivity.this, "Please capture any text", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent chatIntent = new Intent(CameraActivity.this,ShareActivity.class);
                    chatIntent.putExtra("text",text);
                    startActivity(chatIntent);

                    overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                }
            }
        });


        startCameraSource();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }

    private void startCameraSource()
    {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational())
        {
            Log.w(TAG, "Detector dependencies not loaded yet");
        }
        else
        {
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(SurfaceHolder holder)
                {
                    try
                    {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
                {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>()
            {
                @Override
                public void release()
                {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections)
                {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                StringBuilder stringBuilder = new StringBuilder();

                                for(int i=0;i<items.size();i++)
                                {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }

                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}
