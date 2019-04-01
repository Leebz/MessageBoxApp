package com.whut.androidtest.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import rx.functions.Action1;

public class EditMsgActivity extends AppCompatActivity{
    private ImageView btnContact;
    private EditText textReciver;
    private String number;
    private BootstrapButton btn_send;
    private BootstrapEditText text_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_msg);
        RxPermissions.getInstance(EditMsgActivity.this)
                .request(Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(aBoolean){
                            Log.d("PERMISSION","OK");
                        }
                        else{
                            Log.d("DENY","NMO");
                        }
                    }
                });

        textReciver = findViewById(R.id.text_receiver);
        text_content = findViewById(R.id.text_content);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check receiver
                if(TextUtils.isEmpty(textReciver.getText())){
                    textReciver.setError("请选择收件人");
                    return;
                }
                else if(number==null){
                    if(TextUtils.isDigitsOnly(textReciver.getText())){
                        number = textReciver.getText().toString();
                    }
                    else{
                        textReciver.setError("请填写正确的收件人");
                        return;
                    }
                }
                if(TextUtils.isEmpty(text_content.getText())){
                    text_content.setError("请填写短信");
                    return;
                }
                SmsManager sms = SmsManager.getDefault();
                PendingIntent pi = PendingIntent.getBroadcast(EditMsgActivity.this,0,new Intent(),0);
                sms.sendTextMessage(number,null,text_content.getText().toString(),pi,null);
                //Update DB
                String uuid = UUID.randomUUID().toString().replaceAll("-","");
                Log.d("UUID",uuid);
                MsgDetailBean msg = new MsgDetailBean(uuid, text_content.getText().toString(),1, new Date().toLocaleString(), getPureNumber(number),1);

                WriteToFile(msg);

                //jump to chatActivity
                Intent intent = new Intent(EditMsgActivity.this, ChatActivity.class);
                intent.putExtra("partner", getPureNumber(number));
                startActivity(intent);
                //destroy this activity ,because it remain a edit state
                finish();


            }
        });
        btnContact = findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditMsgActivity.this, "Get Contact", Toast.LENGTH_SHORT).show();
//                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),0);
                Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
//                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent,0);
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
    public ArrayList<MsgDetailBean> ReadFromFile(){
        ArrayList<MsgDetailBean> data = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(this.openFileInput("data"));
            data = (ArrayList<MsgDetailBean>)ois.readObject();
            ois.close();
            Log.d("FILE",data.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                KLog.e(usernumber+" ("+username+")");
                textReciver.setText(username);
                number = usernumber;
                Log.d("NUMBER",usernumber);
//                cInviteeTel.setText(usernumber);
            }

        }


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
