package com.whut.androidtest;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.stetho.Stetho;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.util.Iterator;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private TextView text_phone;
    private TextView text_psw;
    private static final String USER_ID = "USERID";
    private static final int READ_CONTACT = 111;
    private static final int SEND_SMS = 112;
    private BootstrapButton btn_register;
    int code1 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        //初始化数据库



        //申请权限
        RxPermissions.getInstance(MainActivity.this)
                .request(Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(aBoolean){
                            Log.d("PERMISSION","OK");
                        }
                        else{
                            Log.d("DENY","NMO");
                        }
                    }
                });

        text_phone = (TextView)findViewById(R.id.phoneNumText);
        text_psw = (TextView)findViewById(R.id.psw_text);
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String logincache = sp.getString("PHONE_NUM","null");
//        保存登陆状态
        if(!logincache.equals("null")){
            Intent intent =  new Intent(MainActivity.this, MsgPreviewActivity.class);
            startActivity(intent);
            finish();
        }
//        startActivity(new Intent(MainActivity.this, MsgPreviewActivity.class));


        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterAcitivity.class));
            }
        });

    }


    //SEND POST REQUEST
    public void attempLogin(View view){
//        Toast.makeText(this, "This is a toast", Toast.LENGTH_SHORT).show();
        String phoneNumber = text_phone.getText().toString();
        String password = text_psw.getText().toString();
        String res;
        Log.d("AAA","点击");
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("type", "login")
                .add("phoneNumber",phoneNumber)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2/android/login.php")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("LLL","onFailure"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("LLL",response.protocol()+" "+response.code()+" "+response.message());
                Headers headers = response.headers();
                for(int i=0;i<headers.size();i++){
                    Log.d("LLL",headers.name(i)+":"+headers.value(i));

                }
                //获取登陆结果
                String res = response.body().string();
                JSONObject json = JSON.parseObject(res);
                Log.d("JSON",res);
                int code = json.getInteger("code");
                code1 = code;
                if(code==200){
                    SharedPreferences sp = getSharedPreferences("USERINFO",0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("PHONE_NUM", phoneNumber);
                    editor.commit();
                    Intent intent =  new Intent(MainActivity.this, MsgPreviewActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(code==404){
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });


    }

}
