package com.whut.androidtest.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.whut.androidtest.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class checkFinger extends AppCompatActivity {
    private static final String TAG = "gryphon";
    private BiometricPrompt mBiometricPrompt;
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback;



    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_finger);
        mBiometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("隐私短信")
                .setDescription("验证指纹以进入隐私短信")
                .setNegativeButton("取消", getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Cancel button clicked");
                        finish();
                    }
                })
                .build();

        mCancellationSignal = new CancellationSignal();
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                Log.i(TAG, "Canceled");
            }
        });

        mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Log.i(TAG, "onAuthenticationError " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i(TAG, "onAuthenticationSucceeded " + result.toString());
                Intent intent = new Intent(checkFinger.this, PrivateDialogList.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Log.i(TAG, "onAuthenticationFailed ");
            }
        };

        mBiometricPrompt.authenticate(mCancellationSignal, getMainExecutor(), mAuthenticationCallback);

    }


}
