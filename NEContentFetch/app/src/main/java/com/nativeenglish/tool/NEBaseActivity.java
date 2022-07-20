package com.nativeenglish.tool;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NEBaseActivity extends AppCompatActivity {
    protected ProgressDialog progressDialog;
    public void showToast(String msg){
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    public void runLater(final Runnable runnable, long delayMillis){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {

                }
            }
        }, delayMillis);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }


}
