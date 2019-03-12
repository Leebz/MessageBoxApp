package com.whut.androidtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView text_phone;
    private TextView text_psw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_phone = (TextView)findViewById(R.id.phoneNumText);
        text_psw = (TextView)findViewById(R.id.psw_text);
    }
    //SEND POST REQUEST
    public void attempLogin(View view){
//        Toast.makeText(this, "This is a toast", Toast.LENGTH_SHORT).show();
        String username = text_phone.getText().toString();
        String password = text_psw.getText().toString();
        String res;
        Log.d("AAA","点击");
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("type", "login")
                .add("username",username)
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
                String res = response.body().string();
                Log.d("LLL","onResponse: "+res.length());
                Intent intent =  new Intent(MainActivity.this, MsgPreviewActivity.class);
                startActivity(intent);


            }
        });

//send Get Request
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://10.0.2.2/test/android/login.php")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("Falue","falure");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if(response.isSuccessful()){
//                    Log.d("kwwl","获取数据成功");
//                    Log.d("kwwl","code()=="+response.code());
//                    Log.d("kwwl","body()=="+response.body().string());
//                }
//                else{
//                    Log.d("kwwl","Failure");
//                }
//            }
//        });
    }
}
