package com.whut.androidtest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.whut.androidtest.Bean.MsgDetailBean;
import com.whut.androidtest.adapter.MsgListAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private MsgListAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView text_user_info;
    private TextView text_input;
    private BootstrapButton btn_send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        String partner = getIntent().getExtras().getString("partner");
        text_user_info = findViewById(R.id.text_receiver);
        text_user_info.setText(partner);
        text_input = findViewById(R.id.text_input);
        btn_send = findViewById(R.id.btn_send);



        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MsgDetailBean> data = getMsgList(partner);


        mAdapter = new MsgListAdapter(R.layout.msg_detail_item, data);
        recyclerView.setAdapter(mAdapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(text_input.getText())){
                    //input is not empty
                    SmsManager sms = SmsManager.getDefault();
                    PendingIntent pi = PendingIntent.getBroadcast(ChatActivity.this,0,new Intent(),0);
                    sms.sendTextMessage(partner,null,text_input.getText().toString(),pi,null);
                    //update local data file
                    MsgDetailBean msg = new MsgDetailBean(text_input.getText().toString(),1, new Date().toLocaleString(), getPureNumber(partner),1);
                    data.add(msg);
                    WriteToFile(msg);
                    //redraw UI
                    mAdapter.notifyDataSetChanged();

                    text_input.setText("");

                }
                else{
                    Toast.makeText(ChatActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    public void WriteToFile(MsgDetailBean entity){
        try {

            ArrayList<MsgDetailBean> list = ReadFromFile();
            ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("data", MODE_PRIVATE));
            list.add(entity);
            oos.writeObject(list);

            oos.flush();
            oos.close();
            Log.d("WRITE",list.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<MsgDetailBean> getMsgList(String partner){
        ArrayList<MsgDetailBean> originData = ReadFromFile();
        ArrayList<MsgDetailBean> res = new ArrayList<>();
        for(MsgDetailBean msg : originData){
            if(msg.getPartner().equals(partner)){
                res.add(msg);
            }
        }

        return res;
    }

    public ArrayList<MsgDetailBean> ReadFromFile(){
        ArrayList<MsgDetailBean> data = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(this.openFileInput("data"));
            data = (ArrayList<MsgDetailBean>)ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;

    }
    public String getPureNumber(String data){
        String res = "";
        for(int i=0;i<data.length();i++){
            if(data.charAt(i)!=' '&&data.charAt(i)!='-'){
                res += data.charAt(i);
            }
        }
        return res;
    }

}
