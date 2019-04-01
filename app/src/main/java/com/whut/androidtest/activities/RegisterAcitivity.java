package com.whut.androidtest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.whut.androidtest.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterAcitivity extends AppCompatActivity {
    private BootstrapButton btn_regiser;
    private TextView phoneNum;
    private TextView password;
    private TextView repeatPsw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitivity);
        phoneNum = findViewById(R.id.phoneNumText);
        password = findViewById(R.id.psw_text);
        repeatPsw = findViewById(R.id.psw_text_repeat);
        btn_regiser = findViewById(R.id.btn_register);
        btn_regiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneNum.getText())||
                        TextUtils.isEmpty(password.getText())||
                        TextUtils.isEmpty(repeatPsw.getText())){
                    Toast.makeText(RegisterAcitivity.this, "请填写全部信息", Toast.LENGTH_SHORT).show();
                }
                else if(!TextUtils.isDigitsOnly(phoneNum.getText())||phoneNum.getText().length()!=11){
                    Toast.makeText(RegisterAcitivity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                }
                else if(!TextUtils.equals(password.getText(), repeatPsw.getText())){
                    Toast.makeText(RegisterAcitivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                }
                else{
                    String phoneNumber = phoneNum.getText().toString();
                    String psw = password.getText().toString();

                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("type","reg")
                            .add("phoneNum",phoneNumber)
                            .add("psw",psw)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://116.62.247.192/Android/login.php")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("NETWORK",e.getStackTrace().toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Log.d("NETWORK",res);
                            JSONObject obj = JSON.parseObject(res);
                            if(obj.getInteger("code")==200){
                                SharedPreferences sp = getSharedPreferences("USERINFO",0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("PHONE_NUM", phoneNumber);
                                editor.commit();
                                startActivity(new Intent(RegisterAcitivity.this, DialogListActivity.class));
                            }

                        }
                    });

                }

            }
        });
    }
}
