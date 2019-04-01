package com.whut.androidtest.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.bean.MsgPreviewBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FileHelper {
    private Context context;
    public FileHelper(Context context) {
        this.context = context;
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
    public void WriteToFile(ArrayList<MsgDetailBean> msgs) {
        try {
            File file = new File(context.getFilesDir().getPath(), "data");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(msgs);
            oos.flush();
            oos.close();
//            Log.d("WRITE",list.size()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void WriteToFile(MsgDetailBean entity){
        try {

            ArrayList<MsgDetailBean> list = ReadFromFile();
            File file = new File(context.getFilesDir().getPath(), "data");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            list.add(entity);
            oos.writeObject(list);

            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<MsgDetailBean> ReadFromFile(){

        ArrayList<MsgDetailBean> data = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir().getPath(), "data");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (ArrayList<MsgDetailBean>)ois.readObject();
            ois.close();
            Log.d("FILE",data.size()+"");
            for(int i=0;i<data.size();i++){
                Log.d("LISTDATA",data.get(i).getId()+" "+data.get(i).getContent()+"  "+data.get(i).getState());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;

    }
    public ArrayList<MsgPreviewBean> castPreview(ArrayList<MsgDetailBean> details){
        ArrayList<MsgPreviewBean> res = new ArrayList<>();
        //按照时间逆序 由新到旧输出
        for(int i=details.size()-1;i>=0;i--){
            MsgDetailBean msg = details.get(i);
            if(msg.getState()!=-1){
                res.add(new MsgPreviewBean(msg.getPartner(),msg.getDate(),getPreviewContent(msg.getContent())));
            }
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
    public void DeleteByPartner(String partner){
        ArrayList<MsgDetailBean> res = ReadFromFile();
        for(MsgDetailBean msg : res){
            if(msg.getPartner().equals(partner)){
                msg.setState(-1);
            }
        }
        WriteToFile(res);

    }
    public void getSmsInPhone(){
        final String SMS_URI_ALL = "content://sms/"; // 所有短信
        ArrayList<MsgDetailBean> msgs = new ArrayList<>();
        try{
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person",
                    "body", "date", "type", };
            Cursor cur = context.getContentResolver().query(uri, projection, null,
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


            WriteToFile(msgs);

        }catch(SQLiteException ex){

        }
    }
}
