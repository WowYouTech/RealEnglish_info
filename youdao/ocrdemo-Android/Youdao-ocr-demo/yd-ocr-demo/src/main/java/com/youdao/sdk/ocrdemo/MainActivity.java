package com.youdao.sdk.ocrdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {
    private Handler mHandler = new Handler();

    TextView startWelcomeCopyright;

    int alpha = 255;

    int b = 0;
    public static final int UNUSED_REQUEST_CODE = 255;  // Acceptable range is [0, 255]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View startBtn = findViewById(R.id.online);
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, OcrDemoActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivityForResult(in, 0); 
            }
        });

        //如果targetSdkVersion设置为>=23的值，则需要申请权限
        if(!isPermissionGranted(this, WRITE_EXTERNAL_STORAGE)){
            String[] perssions = {WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, perssions, UNUSED_REQUEST_CODE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static boolean isPermissionGranted(final Context context,
                                              final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
