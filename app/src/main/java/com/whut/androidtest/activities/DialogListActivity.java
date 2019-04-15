package com.whut.androidtest.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.DialogListAdapter;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.bean.MsgPreviewBean;
import com.whut.androidtest.util.EasyAES;
import com.whut.androidtest.util.FileHelper;
import com.whut.androidtest.util.PermissionUtil;
import com.xw.repo.widget.BounceScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action1;

public class DialogListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static DialogListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MsgPreviewBean> list;
    private FileHelper fileHelper;
    private FloatingActionButton fab;
    private ImageView imgSetting;
    private BounceScrollView bounceScrollView;
    private boolean IsEnterPrivateArea;
    private BroadcastReceiver broadcastReceiver;
    private SearchView searchView;
    private boolean isSearching;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IsEnterPrivateArea = false;
        //Read file and update UI
        Log.d("STRINGSS", "onResume:"+searchView.getQuery()+"isEQUAL: "+searchView.getQuery().equals(""));
        if(list!=null&&searchView.getQuery().toString().equals("")){
//            list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));
            list = fileHelper.getDialogList(fileHelper.ReadFromFile(),this, 0);
            mAdapter.setNewData(list);
            mAdapter.notifyDataSetChanged();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_list);
        IsEnterPrivateArea = false;
        //init fileHelper
        fileHelper = new FileHelper(this);
        //get permission
        PermissionUtil.getPermission(this);
        //get host info
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String host = sp.getString("PHONE_NUM","");
        //first enter check
        boolean is_first = sp.getBoolean("IS_FIRST", false);
        if(is_first==false){
            //first enter app ,read messagebox and write to local file
            fileHelper.getSmsInPhone();
            //write sp
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("IS_FIRST",true);
            editor.apply();
            //check cloud file ,if cloud exist corresponding data ,notify user whether to syn them


        }

        //init recyclerview
        recyclerView = findViewById(R.id.msgs_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));
        list = fileHelper.getDialogList(fileHelper.ReadFromFile(),this,0);

        mAdapter = new DialogListAdapter(R.layout.msg_item, list);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(mAdapter);

        //add fab click event
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(DialogListActivity.this, EditMsgActivity.class));
            }
        });
        //add setting click event
        imgSetting = findViewById(R.id.img_setting);
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DialogListActivity.this, SettingActivity.class));
            }
        });

        //add item click listener
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //navigate to chat activity
                //update read state

                Intent intent = new Intent(DialogListActivity.this, ChatActivity.class);
                intent.putExtra("partner",list.get(position).getPhonenumber());
                startActivity(intent);
            }
        });

        //add item long click
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                //Show Dialog
                new SweetAlertDialog(DialogListActivity.this,SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("删除")
                        .setContentText("您确认删除此对话吗?")
                        .setCancelText("取消")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmText("确认")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                //update local file
                                fileHelper.DeleteByPartner(list.get(position).getPhonenumber(),0);
                                list.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return false;
            }
        });
        //add scroll event
        bounceScrollView = findViewById(R.id.bounceView);
        bounceScrollView.setOnScrollListener(new BounceScrollView.OnScrollListener() {
            @Override
            public void onScrolling(int i, int i1) {
            }
        });
        bounceScrollView.setOnOverScrollListener(new BounceScrollView.OnOverScrollListener() {
            @Override
            public void onOverScrolling(boolean b, int i) {

                if(IsEnterPrivateArea==false&&i>300){
                    IsEnterPrivateArea = true;
                    startActivity(new Intent(DialogListActivity.this, checkFinger.class));
//                    finish();
                }

            }
        });

        searchView = findViewById(R.id.msg_search);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null){
                    imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<MsgDetailBean> msgs = fileHelper.ReadFromFile();
                ArrayList<MsgDetailBean> res = new ArrayList<>();
                isSearching = true;
                for(MsgDetailBean msg : msgs){
                    if(msg.getState()!=-1&&msg.getIsPrivate()==0){
                        if(msg.getContent().contains(s)){
                            res.add(msg);
                        }

                    }
                }

                mAdapter.setNewData(fileHelper.getDialogList(res,DialogListActivity.this, 0));
                mAdapter.notifyDataSetChanged();

                return true;
            }
        });



    }

}
