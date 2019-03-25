package com.whut.androidtest.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.whut.androidtest.R;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Setting extends AppCompatActivity {
    private TextView btn_upload;
    private LinearLayout btn_syn;
    private LinearLayout btn_exit;
    private FileHelper fileHelper = new FileHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        init();
    }
    public void init(){
        btn_upload = findViewById(R.id.btn_upload);
        btn_syn = findViewById(R.id.btn_syn);
        btn_exit = findViewById(R.id.btn_exit);
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String host = sp.getString("PHONE_NUM","");

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Setting.this, "点击事件", Toast.LENGTH_SHORT).show();
                ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                ArrayList<MsgDetailBean> msgTobeSend = new ArrayList<>();
                for(MsgDetailBean msg: msgs){
                    if(msg.getState()==1){
                        msgTobeSend.add(msg);
                    }
                }
                Toast.makeText(Setting.this, "正在备份...", Toast.LENGTH_SHORT).show();
                if(msgTobeSend.size()>0){
                    String jsonStr = JSON.toJSONString(msgTobeSend);
                    Log.d("JSON",jsonStr);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("host",host)
                            .add("data",jsonStr).build();


                    Request request = new Request.Builder()
                            .url("http://10.0.2.2/Android/backup.php")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Log.d("RES",res);
                            JSONObject obj = JSON.parseObject(res);
                            if(obj.getInteger("code")==200){
                                //update local file ,modify msgs state to 9
                                ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                                for(MsgDetailBean msg : msgs){
                                    if(msg.getState()==1){
                                        msg.setState(0);
                                    }
                                }
                                fileHelper.WriteToFile(msgs);
                                //printres
                                ArrayList<MsgDetailBean> datas = fileHelper.ReadFromFile();
                                for(MsgDetailBean data : datas){
                                    Log.d("DATA",data.getPartner()+"  state"+data.getState());
                                }



                            }

                        }
                    });
                }
            }
        });


    }

}
