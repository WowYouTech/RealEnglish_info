package com.youdao.sdk.ydtranslatedemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.lang.reflect.Method;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
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
        View startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TranslateForwardHelper.toTranslateActivity(MainActivity.this);
            }
        });

        View startBtn1 = findViewById(R.id.startBtn1);
        startBtn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateForwardHelper.toHanyuActivity(MainActivity.this);
            }
        });

        View startBtn4 = findViewById(R.id.startBtn4);
        startBtn4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TranslateForwardHelper
                        .toTranslateOfflineActivity(MainActivity.this);
            }
        });

        View startBtn5 = findViewById(R.id.startBtn6);
        startBtn5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TranslateForwardHelper
                        .toSpeachTranslateOfflineActivity(MainActivity.this);
            }
        });

        View startBtn7 = findViewById(R.id.startBtn7);
        startBtn7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TranslateForwardHelper
                        .toOCRTranslateActivity(MainActivity.this);
            }
        });

        checkIfCPUx86();
        //如果targetSdkVersion设置为>=23的值，则需要申请权限
        if (!isPermissionGranted(this, WRITE_EXTERNAL_STORAGE)) {
            String[] perssions = {WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, perssions, UNUSED_REQUEST_CODE);
        }
        if (!isPermissionGranted(this, RECORD_AUDIO)) {
            String[] perssions = {RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, perssions, UNUSED_REQUEST_CODE);
        }
    }

    public static boolean checkIfCPUx86() {
        //1. Check CPU architecture: arm or x86
        if (getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")) {
            //The CPU is x86
            return true;
        } else {
            return false;
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, key, ""));
            Log.i("youdao", "CPU类型--------------->" + value);
        } catch (Exception e) {
        }

        return value;
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
