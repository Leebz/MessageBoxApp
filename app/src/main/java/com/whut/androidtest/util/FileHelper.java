package com.whut.androidtest.util;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whut.androidtest.activities.SettingActivity;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public String getCorrespondingContact(String number){
        String res = "";
        boolean hasContact = false;
        HashMap<String,String> contacts = readContacts(context);

        if(contacts.containsKey(number)){
            res = contacts.get(number);
            hasContact = true;

        }
        if(!hasContact){
            res = number;
        }

        return res;
    }
    public ArrayList<MsgPreviewBean> getDialogList(ArrayList<MsgDetailBean> msgs, Context context, int isPrivate){
        ArrayList<MsgPreviewBean> res = new ArrayList<>();
        ArrayList<String> IsIn = new ArrayList<>();
        HashMap<String,String> contacts = readContacts(context);

        for(int i=msgs.size()-1; i>=0;i--){
            MsgDetailBean msg = msgs.get(i);
            if(!IsIn.contains(msg.getPartner())&&msg.getIsPrivate()==isPrivate&&msg.getState()!=-1){
                IsIn.add(msg.getPartner());
                String username = getCorrespondingContact(msg.getPartner());
                MsgPreviewBean previewBean = new MsgPreviewBean(username, msg.getPartner(), msg.getDate(), getPreviewContent(msg.getContent()));
                previewBean.setHasUnreadMsg(msg.getIsRead());
                res.add(previewBean);

            }
            else if(IsIn.contains(msg.getPartner())&&msg.getIsRead()==1&&msg.getIsPrivate()==isPrivate&&msg.getState()!=-1){
                for(MsgPreviewBean msgPreviewBean : res ){
                    if(msgPreviewBean.getUsername().equals(msg.getPartner())){
                        msgPreviewBean.setHasUnreadMsg(1);
                    }
                }
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

    public String getPreviewContent(String content){
        String res = content;
        if(content.length()>40){
            res = content.substring(0,40)+"...";
        }
        return res;
    }
    // 将需要删除的短信标记为-1
    public void DeleteByPartner(String partner, int isPrivate){
        ArrayList<MsgDetailBean> res = ReadFromFile();
        for(MsgDetailBean msg : res){
            if(msg.getPartner().equals(partner)&&msg.getIsPrivate()==isPrivate){
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
                    MsgDetailBean msg = new MsgDetailBean(varid+"", strbody, intType, strDate, strAddress, 1,0, 0);
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
    public ArrayList<MsgDetailBean> getMsgList(String partner ,int isPrivate){
        ArrayList<MsgDetailBean> originData = ReadFromFile();
        ArrayList<MsgDetailBean> res = new ArrayList<>();
        for(MsgDetailBean msg : originData){
            if(msg.getPartner().equals(partner)&&msg.getIsPrivate()==isPrivate){
                res.add(msg);
            }
        }

        return res;
    }
    public void updateReadState(String partner, int isPrivate){
        ArrayList<MsgDetailBean> list = ReadFromFile();
        for(MsgDetailBean msg : list){
            if(msg.getPartner().equals(partner)){
                if(msg.getState()==0&&msg.getIsRead()==1){
                    msg.setState(2);
                }
                msg.setIsRead(0);

            }
        }
        WriteToFile(list);

    }

    public String ProcessNumber(String number){

        number =  number.replaceAll("\\+86","");
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<number.length();i++){
            if(number.charAt(i)<='9'&&number.charAt(i)>='0'){
                sb.append(number.charAt(i));
            }
        }

        return sb.toString();

    }
    public HashMap<String,String> readContacts(Context context){
        HashMap<String, String> res = new HashMap<>();
        Cursor cursor = null;
        try{
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String displayname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    res.put(ProcessNumber(number), displayname);

                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor==null){
                cursor.close();
            }
        }

        return  res;
    }

    public boolean hasBackup(){

        ArrayList<MsgDetailBean> msgs = ReadFromFile();
        for(MsgDetailBean msg : msgs){
            if(msg.getState()!=0){
                return false;
            }
        }

        return true;
    }

    public void backup(Context context, String host){


        ArrayList<MsgDetailBean> msgs = ReadFromFile();
        for(int i=0;i<msgs.size();i++){
            Log.d("INFO",msgs.get(i).getId()+"  "+msgs.get(i).getContent()+"  "+msgs.get(i).getState());

        }
        ArrayList<MsgDetailBean> msgTobeSend = new ArrayList<>();
        ArrayList<MsgDetailBean> msgTobeDelete =  new ArrayList<>();
        ArrayList<MsgDetailBean> msgTobeModified =  new ArrayList<>();
        for (MsgDetailBean msg : msgs) {
            if (msg.getState() == 1) {
                msgTobeSend.add(msg);
            }
            else if(msg.getState()==-1){
                msgTobeDelete.add(msg);
            }
            else if(msg.getState() == 2){
                msgTobeModified.add(msg);
            }


        }
        if(msgTobeSend.size() == 0&&msgTobeDelete.size() == 0&&msgTobeModified.size() == 0){
            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("提示")
                    .setContentText("没有数据需要备份啦,所有数据都很安全")
                    .setConfirmText("好的")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
        }
        else if(msgTobeSend.size() > 0||msgTobeDelete.size() > 0||msgTobeModified.size()>0){
            Dialog dialog = CustomProgressDialog.createLoadingDialog(context, "正在备份中...");
            dialog.setCancelable(false);
            dialog.show();

            String jsonStr = JSON.toJSONString(msgTobeSend);
            String jsonDelete = JSON.toJSONString(msgTobeDelete);
            String jsonModify = JSON.toJSONString(msgTobeModified);
            Log.d("JSON", "JSON  "+jsonStr);
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("host", host)
                    .add("data", jsonStr)
                    .add("delete", jsonDelete)
                    .add("modify", jsonModify)
                    .build();
            Request request = new Request.Builder()
//                  .url("http://10.0.2.2/Android/backup.php")
                    .url("http://116.62.247.192/Android/backup.php")
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    Log.d("RES", res);
                    try{
                        JSONObject obj = JSON.parseObject(res);
                        if (obj.getInteger("code") == 200) {
                            //update local file ,modify msgs state to 0
                            ArrayList<MsgDetailBean> msgs = ReadFromFile();
                            for (MsgDetailBean msg : msgs) {
                                if (msg.getState() == 1||msg.getState() == 2) {
                                    msg.setState(0);
                                }

                            }
                            Iterator<MsgDetailBean> iterator = msgs.iterator();
                            while (iterator.hasNext()){
                                if(iterator.next().getState()==-1){
                                    iterator.remove();
                                }
                            }

                            WriteToFile(msgs);

                            ArrayList<MsgDetailBean> datas = ReadFromFile();
                            for (MsgDetailBean data : datas) {
                                Log.d("DATA", data.getPartner() + "  state" + data.getState());
                            }
                            Thread.sleep(1000);
                            dialog.dismiss();



                        }
                        if(obj.getInteger("code")==400){
                            Toast.makeText(context, "WRONG", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){

                    }
                }

            });
        }

    }
    public void syn(Context context, String host){
        //check backup state
        if(!hasBackup()){
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("本地仍有未备份的数据，是否继续恢复?")
                    .setConfirmText("好的")
                    .setCancelText("算了")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            downloadMsgs(host);
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
        else{
            downloadMsgs(host);
        }


    }

    public void downloadMsgs(String host){
        Toast.makeText(context, "数据同步中", Toast.LENGTH_SHORT).show();
        Dialog dialog = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        dialog.setCancelable(false);
        dialog.show();
        //send post request
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("host", host)
                .build();
        Request request = new Request.Builder()
                .url("http://116.62.247.192/Android/syn.php")
//              .url("http://10.0.2.2/Android/syn.php")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("SYNRES",res);
                JSONObject obj = JSON.parseObject(res);
                if(obj.getInteger("code")==200){
                    JSONArray array = obj.getJSONArray("data");
                    Log.d("DATASIZE",array.size()+" ");
                    ArrayList<MsgDetailBean> msgs = new ArrayList<>();
                    for(int i=0;i<array.size();i++){
                        JSONObject item = array.getJSONObject(i);
                        String local_id = item.getString("local_id");
                        String content = item.getString("content");
                        String type = item.getString("type");
                        String partner = item.getString("partner");
                        String time = item.getString("time");
                        String state = item.getString("state");
                        String isPrivate = item.getString("isPrivate");
                        String isRead = item.getString("isRead");

                        MsgDetailBean msg = new MsgDetailBean(local_id, content, Integer.parseInt(type), time, partner, Integer.parseInt(state),Integer.parseInt(isPrivate), Integer.parseInt(isRead));
                        msgs.add(msg);

                    }
                    WriteToFile(msgs);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();

                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTime(ArrayList<MsgDetailBean> msgs){
        msgs.sort(new Comparator<MsgDetailBean>() {
            @Override
            public int compare(MsgDetailBean o1, MsgDetailBean o2) {

                return 0;
            }
        });
    }
}
