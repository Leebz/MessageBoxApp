package com.whut.androidtest.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whut.androidtest.R;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.util.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {
    private TextView btn_upload;
    private LinearLayout btn_syn;
    private LinearLayout btn_exit;
    private FileHelper fileHelper = new FileHelper(this);
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        init();
    }

    public void init() {
        progressBar = findViewById(R.id.progressBar);
        btn_upload = findViewById(R.id.btn_upload);
        btn_syn = findViewById(R.id.btn_syn);
        btn_exit = findViewById(R.id.btn_exit);
        SharedPreferences sp = getSharedPreferences("USERINFO", 0);
        String host = sp.getString("PHONE_NUM", "");

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileHelper.backup(SettingActivity.this, host);

            }
        });

        btn_syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileHelper.syn(SettingActivity.this, host);


            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否注销登录")
                        .setConfirmText("好的")
                        .setCancelText("再等等")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(SettingActivity.this, MainActivity.class));

                                finish();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();

            }
        });


    }

}
