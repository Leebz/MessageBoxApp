package com.whut.androidtest.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Looper;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.DialogListAdapter;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.util.FileHelper;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
    int code1;
    private FileHelper fileHelper = new FileHelper(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
//        if(checkDefaultSettings()){
//            getPermission();
//
//        }
//        else{
//            finish();
//        }
        getPermission();





        text_phone = (TextView)findViewById(R.id.phoneNumText);
        text_psw = (TextView)findViewById(R.id.psw_text);
        //获取登陆状态
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String logincache = sp.getString("PHONE_NUM","null");
        if(!logincache.equals("null")){

            Intent intent =  new Intent(MainActivity.this, DialogListActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            //获取短信

        }




        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterAcitivity.class));
            }
        });

    }

    public void getPermission(){
        //申请权限
        RxPermissions.getInstance(MainActivity.this)
                .request(Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
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
    }
    private boolean checkDefaultSettings() {

        boolean isDefault = false;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                checkPermissions();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
//                                checkPermissions();
                            }
                        });
                builder.show();

                isDefault = false;
            } else
                isDefault = true;
        }
        return isDefault;
    }


    //SEND POST REQUEST
    public void attempLogin(View view){
//        Toast.makeText(this, "This is a toast", Toast.LENGTH_SHORT).show();
        String phoneNumber = text_phone.getText().toString();
        String password = text_psw.getText().toString();
        String res;

        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("type", "login")
                .add("phoneNumber",phoneNumber)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://116.62.247.192/Android/login.php")
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
                    Intent intent =  new Intent(MainActivity.this, DialogListActivity.class);
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
