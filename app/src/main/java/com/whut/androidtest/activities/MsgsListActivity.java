package com.whut.androidtest.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.recyclerAdapter;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.bean.MsgPreviewBean;
import com.whut.androidtest.util.FileHelper;
import com.xw.repo.widget.BounceScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class MsgsListActivity extends AppCompatActivity {
    private BounceScrollView bounceScrollView;
    private recyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FileHelper fileHelper = new FileHelper(this);
    private ArrayList<MsgPreviewBean> list;
    private ImageView setting;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_msgs_list);
        getSmsInPhone();
        init();



    }
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

                    MsgDetailBean msgBean = new MsgDetailBean(uuid, msg.getDisplayMessageBody(), 0,
                            new Date().toLocaleString(),msg.getOriginatingAddress(),1);
                    fileHelper.WriteToFile(msgBean);
                    //update UI
                    list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME","RESUME");
        list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));
        adapter.notifyDataSetChanged();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);


    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    public void init(){
        fab = findViewById(R.id.fab);

        bounceScrollView = findViewById(R.id.bounceView);
        bounceScrollView.setOnScrollListener(new BounceScrollView.OnScrollListener() {
            @Override
            public void onScrolling(int i, int i1) {
                Log.d("SCORO",i+"  "+i1);
            }
        });
        bounceScrollView.setOnOverScrollListener(new BounceScrollView.OnOverScrollListener() {
            @Override
            public void onOverScrolling(boolean b, int i) {
                Log.d("OVER",""+i);
                if(b==true&&i==300){
                    Toast.makeText(MsgsListActivity.this, "SSSSSSSSSSSSSSSSSS", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MsgsListActivity.this, checkFinger.class));
//                    finish();
                }
            }
        });

        list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));


        RecyclerView recyclerView = findViewById(R.id.msgs_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter  = new recyclerAdapter(list, this);
        ((recyclerAdapter)adapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(adapter);

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgsListActivity.this, SettingActivity.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgsListActivity.this, EditMsgActivity.class));
            }
        });


    }
    public void getSmsInPhone(){
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        ArrayList<MsgDetailBean> msgs = new ArrayList<>();
        try{
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person",
                    "body", "date", "type", };
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
                int id = cur.getColumnIndexOrThrow("_id");
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    Long varid = cur.getLong(id);
                    String strAddress = cur.getString(index_Address);
                    if(strAddress.contains("+86")){
                        strAddress = strAddress.replace("+86","");
                    }
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);
                    if(intType==1){
                        intType = 0;
                    }
                    else {
                        intType = 1;
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);
                    MsgDetailBean msg = new MsgDetailBean(varid+"", strbody, intType, strDate, strAddress, 1);
//                    Log.d("MSG",varid+" "+intType+" "+"  "+strbody);
                    msgs.add(msg);

                }
                while (cur.moveToNext());
                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            }
            //为了与代码结构相同 调整顺序
            ArrayList<MsgDetailBean> res = new ArrayList<>();
            Collections.reverse(msgs);
//            for(int i=msgs.size()-1;i>=0;i--){
//                MsgDetailBean msg = msgs.get(i);
//
//                res.add(msg);
//            }

            fileHelper.WriteToFile(msgs);

        }catch(SQLiteException ex){

        }
    }
}
