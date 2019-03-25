package com.whut.androidtest.util;

import android.content.Context;
import android.util.Log;

import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.bean.MsgPreviewBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
            ArrayList<MsgDetailBean> list = ReadFromFile();
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
}
