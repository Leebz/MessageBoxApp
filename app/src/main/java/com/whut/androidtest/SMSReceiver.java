package com.whut.androidtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.whut.androidtest.activities.ChatActivity;
import com.whut.androidtest.activities.DialogListActivity;
import com.whut.androidtest.adapter.DialogListAdapter;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.bean.MsgPreviewBean;
import com.whut.androidtest.util.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SMSReceiver extends BroadcastReceiver{
//    FileOutputStream outputStream;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        FileHelper fileHelper = new FileHelper(context);


        Log.d("SMS","NEW SMS");
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if(bundle!=null){


            Object[] smsObj = (Object[])bundle.get("pdus");
            for(Object object:smsObj){
                msg = SmsMessage.createFromPdu((byte[]) object);

                Log.d("SMS CONTENT",msg.getOriginatingAddress()+" "+msg.getDisplayMessageBody());
                //insert to db
                String uuid = UUID.randomUUID().toString().replaceAll("-","");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = new Date();
                String strDate = dateFormat.format(d);

                MsgDetailBean msgBean = new MsgDetailBean(uuid, msg.getDisplayMessageBody(), 0,
                        strDate,fileHelper.ProcessNumber(msg.getOriginatingAddress()),1,0, 1);
                Log.d("NEWMSG", msgBean.getPartner()+"  "+msgBean.getState()+"  "+msgBean.getIsRead());





                if(ChatActivity.mAdapter!=null&&ChatActivity.partner!=null&&ChatActivity.partner.equals(msgBean.getPartner())){
                    msgBean.setIsRead(0);
                    fileHelper.WriteToFile(msgBean);
                    ArrayList<MsgDetailBean> chatList = fileHelper.getMsgList(msgBean.getPartner(),0);
                    ChatActivity.mAdapter.setNewData(chatList);
                    ChatActivity.mAdapter.notifyDataSetChanged();
                }
                else{
                    fileHelper.WriteToFile(msgBean);
                    //未与该用户聊天时 发送通知
                    String user = fileHelper.getCorrespondingContact(msgBean.getPartner());
                    String notificationBody = fileHelper.getPreviewContent(msgBean.getContent());
                    //SEND NOTIFICATION
                    NotificationChannel notificationChannel = new NotificationChannel("yunxin","新短信", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(notificationChannel);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"yunxin")
                            .setSmallIcon(R.drawable.launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.launcher))
                            .setAutoCancel(true)
                            .setContentTitle(user)
                            .setContentText(notificationBody);
                    Intent resultIntent = new Intent(context, ChatActivity.class);
                    resultIntent.putExtra("partner",msgBean.getPartner());
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(ChatActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(123,builder.build());
                }
                //UPDATE UI

                if(DialogListActivity.mAdapter!=null){
                    ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                    ArrayList<MsgPreviewBean> list = fileHelper.getDialogList(msgs, context, 0);
                    DialogListActivity.mAdapter.setNewData(list);
                    DialogListActivity.mAdapter.notifyDataSetChanged();
                }


            }
        }

    }




}
