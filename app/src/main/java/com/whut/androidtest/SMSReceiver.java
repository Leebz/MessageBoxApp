package com.whut.androidtest;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.whut.androidtest.Bean.MsgDetailBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver{
//    FileOutputStream outputStream;


    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("SMS","NEW SMS");
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if(bundle!=null){


            Object[] smsObj = (Object[])bundle.get("pdus");
            for(Object object:smsObj){
                msg = SmsMessage.createFromPdu((byte[]) object);

                Log.d("SMS CONTENT",msg.getOriginatingAddress()+" "+msg.getDisplayMessageBody());
                //insert to db
                MsgDetailBean msgBean = new MsgDetailBean(msg.getDisplayMessageBody(), 0,
                        new Date().toLocaleString(),msg.getOriginatingAddress(),1);
//                File file = new File(context.getFilesDir(), "data");
                WriteToFile(msgBean, context);





            }
        }

    }
    public void WriteToFile(MsgDetailBean entity, Context context){
        try {

            ArrayList<MsgDetailBean> list = ReadFromFile(context);
            File file = new File(context.getFilesDir().getPath(), "data");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            list.add(entity);
            oos.writeObject(list);

            oos.flush();
            oos.close();
            Log.d("WRITE",list.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<MsgDetailBean> ReadFromFile(Context context){

        ArrayList<MsgDetailBean> data = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir().getPath(), "data");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
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



}
