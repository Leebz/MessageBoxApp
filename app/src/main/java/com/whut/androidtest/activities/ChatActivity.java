package com.whut.androidtest.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.ChatListAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChatActivity extends AppCompatActivity {
    private ChatListAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView text_user_info;
    private BootstrapEditText text_input;
    private BootstrapButton btn_send;
    private String partner;
    private ArrayList<MsgDetailBean> data;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RECEIVE","短信类了");
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if(bundle!=null){
                Object[] smsObj = (Object[])bundle.get("pdus");
                for(Object object:smsObj){
                    msg = SmsMessage.createFromPdu((byte[]) object);

                    Log.d("短信内容",msg.getOriginatingAddress()+" "+msg.getDisplayMessageBody());
                    //write to file
                    String uuid = UUID.randomUUID().toString().replaceAll("-","");

                    MsgDetailBean msgBean = new MsgDetailBean(uuid,msg.getDisplayMessageBody(), 0,
                            new Date().toLocaleString(),msg.getOriginatingAddress(),1);
                    WriteToFile(msgBean);
                    //update UI
                    if(partner.equals(msgBean.getPartner())){
                        data.add(msgBean);
                        mAdapter.notifyDataSetChanged();
                    }





                }
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //Register boardcast

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        partner = getIntent().getExtras().getString("partner");
        text_user_info = findViewById(R.id.text_receiver);
        text_user_info.setText(partner);
        text_input = findViewById(R.id.text_input);
        btn_send = findViewById(R.id.btn_send);



        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        data = getMsgList(partner);


        mAdapter = new ChatListAdapter(R.layout.msg_detail_item, data);
        mAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("删除")
                        .setContentText("确认删除此对话吗?")
                        .setConfirmText("确认")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                data.get(position).setState(-1);
                                mAdapter.notifyDataSetChanged();
                                //update local file

                                sweetAlertDialog.dismissWithAnimation();
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
                    MsgDetailBean msg = new MsgDetailBean(uuid, text_input.getText().toString(),1, new Date().toLocaleString(), getPureNumber(partner),1);
                    data.add(msg);
                    WriteToFile(msg);
                    //redraw UI
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
