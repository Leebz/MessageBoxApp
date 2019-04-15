package com.whut.androidtest.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ServiceWorkerWebSettings;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.ChatListAdapter;
import com.whut.androidtest.util.FileHelper;
import com.whut.androidtest.util.PermissionUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action1;

public class ChatActivity extends AppCompatActivity {
    public static ChatListAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView text_user_info;
    private BootstrapEditText text_input;
    private BootstrapButton btn_send;
    public static String partner;
    private ArrayList<MsgDetailBean> data;
    private FileHelper fileHelper;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        //get permissioin
        PermissionUtil.getPermission(this);
        //init fileHelper
        fileHelper = new FileHelper(ChatActivity.this);
        partner = getIntent().getExtras().getString("partner");
        text_user_info = findViewById(R.id.text_receiver);
        //get corresponding name
        HashMap<String, String> contacts = fileHelper.readContacts(this);
        if(contacts.containsKey(partner)){
            text_user_info.setText(contacts.get(partner));
        }
        else {
            text_user_info.setText(partner);
        }

        text_input = findViewById(R.id.text_input);
        btn_send = findViewById(R.id.btn_send);



        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        data = fileHelper.getMsgList(partner, 0);
        fileHelper.updateReadState(partner, 0);//update read state here


        mAdapter = new ChatListAdapter(R.layout.msg_detail_item, data);
        mAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                String []options = {"删除","设为隐私短信"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("删除")
                                    .setContentText("确认删除此对话吗?")
                                    .setConfirmText("确认")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            String id = data.get(position).getId();
                                            data.remove(position);
                                            mAdapter.notifyDataSetChanged();
                                            //update local file

                                            ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                                            for(MsgDetailBean msg : msgs){
                                                if(msg.getId().equals(id)){
                                                    msg.setState(-1);
                                                }
                                            }
                                            fileHelper.WriteToFile(msgs);


                                            sweetAlertDialog.dismiss();
                                            if(data.size()==0){
                                                startActivity(new Intent(ChatActivity.this, DialogListActivity.class));
                                                finish();
                                            }
                                        }
                                    })
                                    .setCancelText("取消")
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }
                        if(which==1){
                            new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("确定设置为隐私短信吗")
                                    .setConfirmText("好的")
                                    .setCancelText("算了")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            data.get(position).setIsPrivate(1);
                                            ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();

                                            for(MsgDetailBean msg:msgs){
                                                if(msg.getId().equals(data.get(position).getId())){
                                                    msg.setIsPrivate(1);
                                                    //未同步时仍然是新增状态
                                                    if(msg.getState()==0){
                                                        msg.setState(2);
                                                    }
                                                }
                                            }
                                            fileHelper.WriteToFile(msgs);
                                            data.remove(position);
                                            mAdapter.notifyDataSetChanged();
                                            sweetAlertDialog.dismissWithAnimation();
                                            if(data.size()==0){
                                                startActivity(new Intent(ChatActivity.this, DialogListActivity.class));
                                                finish();
                                            }

                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();

                                        }
                                    })
                                    .show();
                        }
                    }
                }).show();

                return false;
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(data.size()-1);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(text_input.getText())){
                    //input is not empty
                    SmsManager sms = SmsManager.getDefault();
                    PendingIntent pi = PendingIntent.getBroadcast(ChatActivity.this,0,new Intent(),0);
                    sms.sendTextMessage(partner,null,text_input.getText().toString(),pi,null);
                    //update local data file
                    String uuid = UUID.randomUUID().toString().replaceAll("-","");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date();
                    String strDate = dateFormat.format(d);
                    MsgDetailBean msg = new MsgDetailBean(uuid, text_input.getText().toString(),1, strDate, fileHelper.ProcessNumber(partner),1, 0 ,0);
                    data.add(msg);
                    fileHelper.WriteToFile(msg);
                    //redraw UI
                    mAdapter.setNewData(data);
                    mAdapter.notifyDataSetChanged();
                    text_input.setText("");

                }
                else{
                    text_input.setError("请输入消息内容");
//                    Toast.makeText(ChatActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOP", "onStop: ");
        mAdapter = null;
        partner = null;
    }
}
