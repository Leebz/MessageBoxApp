package com.whut.androidtest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.whut.androidtest.Bean.MsgDetailBean;
import com.whut.androidtest.adapter.MsgListAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private MsgListAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView text_user_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        String partner = getIntent().getExtras().getString("partner");
        text_user_info = findViewById(R.id.text_receiver);
        text_user_info.setText(partner);


        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MsgDetailBean> data = getMsgList(partner);
//        String content = "";
//        for(int i=0;i<5;i++){
//            content = content + "信息长度测试,";
//            MsgDetailBean entity = new MsgDetailBean(content,i%2,"2019-01-02","15912345678",1);
//            data.add(entity);
//
//       }

        mAdapter = new MsgListAdapter(R.layout.msg_detail_item, data);
        recyclerView.setAdapter(mAdapter);




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

}
