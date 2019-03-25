package com.whut.androidtest;

import android.app.Activity;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whut.androidtest.Bean.MsgDetailBean;
import com.whut.androidtest.Bean.UserBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditMsgActivity extends AppCompatActivity{
    private ImageView btnContact;
    private TextView textReciver;
    private String number;
    private Button btn_send;
    private TextView text_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_msg);

        textReciver = findViewById(R.id.text_receiver);
        text_content = findViewById(R.id.text_content);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check receiver
                if(TextUtils.isEmpty(textReciver.getText())){
                    Toast.makeText(EditMsgActivity.this, "请填写正确的收件人", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(number==null){
                    number = textReciver.getText().toString();
                }
                SmsManager sms = SmsManager.getDefault();
                PendingIntent pi = PendingIntent.getBroadcast(EditMsgActivity.this,0,new Intent(),0);
                sms.sendTextMessage(number,null,text_content.getText().toString(),pi,null);
                //Update DB
                MsgDetailBean msg = new MsgDetailBean(text_content.getText().toString(),1, new Date().toLocaleString(), getPureNumber(number),1);

                WriteToFile(msg);
//                ReadFromFile();
//                new InsertAsyncTask().execute(msg);
//                new QueryAsyncTask().execute();
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
