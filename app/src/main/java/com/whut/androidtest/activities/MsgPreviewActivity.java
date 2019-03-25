package com.whut.androidtest.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.MsgPreviewListAdapter;
import com.whut.androidtest.bean.MsgPreviewBean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MsgPreviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MsgPreviewListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton btnAddMsg;
    private FloatingActionButton btnBackup;
    private FloatingActionButton btnSyn;
    private HashMap<String, String> mContact;
    private List<MsgPreviewBean> list;
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
                    list = getPreviewData(castPreview(ReadFromFile()));
                    mAdapter.setNewData(list);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME","RESUME");
        list = getPreviewData(castPreview(ReadFromFile()));
        mAdapter.setNewData(list);

        mAdapter.notifyDataSetChanged();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_msg_preview);
        //update Contact
//        mContact = getContactPhoneNumber();
        //destroy mainactivity
        Log.d("CREATE","CREATE");
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String host = sp.getString("PHONE_NUM","");

        btnBackup = findViewById(R.id.backup);
        btnSyn = findViewById(R.id.syn);


//        Log.d("CONTACT", mContact.size()+"");
        recyclerView = (RecyclerView)findViewById(R.id.msg_preview_list);

        recyclerView.setHasFixedSize(true);
        //use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //获取消息预览列表
        list = getPreviewData(castPreview(ReadFromFile()));
        mAdapter = new MsgPreviewListAdapter(R.layout.msg_item, list);
        //set item click listener
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MsgPreviewActivity.this, "onItemClick"+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MsgPreviewActivity.this, ChatActivity.class);
                intent.putExtra("partner",list.get(position).getUsername());

                startActivity(intent);
            }
        });



        //Add MsgsListActivity to delete Listener
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d("SWIPED",pos+"");

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {



            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        };
        //Enable swipe delete
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        //set load animation
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(mAdapter);





        btnAddMsg = (FloatingActionButton)findViewById(R.id.fab);
        btnAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MsgPreviewActivity.this, EditMsgActivity.class);
                startActivity(intent);
            }
        });

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push local data where state==1 to server
                ArrayList<MsgDetailBean> msgs = ReadFromFile();
                ArrayList<MsgDetailBean> msgTobeSend = new ArrayList<>();
                for(MsgDetailBean msg: msgs){
                    if(msg.getState()==1){
                        msgTobeSend.add(msg);
                    }
                }
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
                                ArrayList<MsgDetailBean> msgs = ReadFromFile();
                                for(MsgDetailBean msg : msgs){
                                    if(msg.getState()==1){
                                        msg.setState(0);
                                    }
                                }
                                WriteToFile(msgs);
                                //printres
                                ArrayList<MsgDetailBean> datas = ReadFromFile();
                                for(MsgDetailBean data : datas){
                                    Log.d("DATA",data.getPartner()+"  state"+data.getState());
                                }



                            }

                        }
                    });
                }


            }
        });
        btnSyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MsgPreviewActivity.this, MsgsListActivity.class));
            }
        });



    }
    private HashMap getContactPhoneNumber(){
        HashMap<String,String> data = new HashMap<>();
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(nameFieldColumnIndex);
            String number = cursor.getString(numberFieldColumnIndex);
            data.put(name,number);
        }
        return data;
    }

    public ArrayList<MsgPreviewBean> getPreviewData(ArrayList<MsgPreviewBean> originData){
        ArrayList<MsgPreviewBean> res = new ArrayList<>();
        ArrayList<String> partners = new ArrayList<>();

        //get preivew List
        for(MsgPreviewBean preview : originData){
            if(!partners.contains(preview.getUsername())){
                partners.add(preview.getUsername());
                res.add(preview);
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
    public void updateFile(){

    }
    public ArrayList<MsgPreviewBean> castPreview(ArrayList<MsgDetailBean> details){
        ArrayList<MsgPreviewBean> res = new ArrayList<>();
        //按照时间逆序 由新到旧输出
        for(int i=details.size()-1;i>=0;i--){
            MsgDetailBean msg = details.get(i);

            res.add(new MsgPreviewBean(msg.getPartner(),msg.getDate(),getPreviewContent(msg.getContent())));
        }


        return res;
    }
    public String getPreviewContent(String content){
        String res = content;
        if(content.length()>40){
            res = content.substring(0,40)+"...";
        }
        return res;
    }
    public void WriteToFile(ArrayList<MsgDetailBean> msgs){
        try {

            ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("data", MODE_PRIVATE));
            oos.writeObject(msgs);
            oos.flush();
            oos.close();
//            Log.d("WRITE",list.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void WriteToFile(MsgDetailBean msg){
        try {

            ArrayList<MsgDetailBean> list = ReadFromFile();
            ObjectOutputStream oos = new ObjectOutputStream(this.openFileOutput("data", MODE_PRIVATE));
            list.add(msg);
            oos.writeObject(list);

            oos.flush();
            oos.close();
            Log.d("WRITE",list.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
