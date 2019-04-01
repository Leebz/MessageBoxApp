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
import java.util.function.Predicate;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        init();
    }

    public void init() {
        btn_upload = findViewById(R.id.btn_upload);
        btn_syn = findViewById(R.id.btn_syn);
        btn_exit = findViewById(R.id.btn_exit);
        SharedPreferences sp = getSharedPreferences("USERINFO", 0);
        String host = sp.getString("PHONE_NUM", "");

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(SettingActivity.this, "点击事件", Toast.LENGTH_SHORT).show();
                ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                for(int i=0;i<msgs.size();i++){
                    Log.d("INFO",msgs.get(i).getId()+"  "+msgs.get(i).getContent()+"  "+msgs.get(i).getState());

                }
                ArrayList<MsgDetailBean> msgTobeSend = new ArrayList<>();
                ArrayList<MsgDetailBean> msgTobeDelete =  new ArrayList<>();
                for (MsgDetailBean msg : msgs) {
                    if (msg.getState() == 1) {
                        msgTobeSend.add(msg);
                    }
                    else if(msg.getState()==-1){
                        msgTobeDelete.add(msg);
                    }

                }
                Toast.makeText(SettingActivity.this, "正在备份...", Toast.LENGTH_SHORT).show();
                if (msgTobeSend.size() > 0||msgTobeDelete.size() > 0) {
                    String jsonStr = JSON.toJSONString(msgTobeSend);
                    String jsonDelete = JSON.toJSONString(msgTobeDelete);
                    Log.d("JSON", "JSON  "+jsonDelete);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("host", host)
                            .add("data", jsonStr)
                            .add("delete", jsonDelete)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://116.62.247.192/Android/backup.php")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Log.d("RES", res);
                            try{
                                JSONObject obj = JSON.parseObject(res);
                                if (obj.getInteger("code") == 200) {
                                    //update local file ,modify msgs state to 9
                                    ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                                    for (MsgDetailBean msg : msgs) {
                                        if (msg.getState() == 1) {
                                            msg.setState(0);
                                        }

                                    }
                                    Iterator<MsgDetailBean> iterator = msgs.iterator();
                                    while (iterator.hasNext()){
                                        if(iterator.next().getState()==-1){
                                            iterator.remove();
                                        }
                                    }

                                    fileHelper.WriteToFile(msgs);
                                    //printres
                                ArrayList<MsgDetailBean> datas = fileHelper.ReadFromFile();
                                for (MsgDetailBean data : datas) {
                                    Log.d("DATA", data.getPartner() + "  state" + data.getState());
                                }


                                }
                                if(obj.getInteger("code")==400){
                                    Toast.makeText(SettingActivity.this, "WRONG", Toast.LENGTH_SHORT).show();
                                }

                            }catch (Exception e){

                            }
                        }

                    });
                }
            }
        });

        btn_syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "数据同步中", Toast.LENGTH_SHORT).show();
                //send post request
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("host", host)
                        .build();
                Request request = new Request.Builder()
                        .url("http://116.62.247.192/Android/syn.php")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.d("SYNRES",res);
                        JSONObject obj = JSON.parseObject(res);
                        if(obj.getInteger("code")==200){
                            JSONArray array = obj.getJSONArray("data");
                            Log.d("DATASIZE",array.size()+" ");
                            ArrayList<MsgDetailBean> msgs = new ArrayList<>();
                            for(int i=0;i<array.size();i++){
                                JSONObject item = array.getJSONObject(i);
                                String local_id = item.getString("local_id");
                                String content = item.getString("content");
                                String type = item.getString("type");
                                String partner = item.getString("partner");
                                String time = item.getString("time");
                                String state = item.getString("state");

                                MsgDetailBean msg = new MsgDetailBean(local_id, content, Integer.parseInt(type), time, partner, Integer.parseInt(state));
                                msgs.add(msg);

                            }
                            fileHelper.WriteToFile(msgs);
                        }
                    }
                });

            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                
                finish();
            }
        });


    }

}
