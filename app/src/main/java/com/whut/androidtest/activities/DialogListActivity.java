package com.whut.androidtest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.DialogListAdapter;
import com.whut.androidtest.bean.MsgPreviewBean;
import com.whut.androidtest.util.FileHelper;
import com.xw.repo.widget.BounceScrollView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action1;

public class DialogListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DialogListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MsgPreviewBean> list;
    private FileHelper fileHelper;
    private FloatingActionButton fab;
    private ImageView imgSetting;
    private BounceScrollView bounceScrollView;
    private boolean IsEnterPrivateArea;

    @Override
    protected void onResume() {
        super.onResume();
        IsEnterPrivateArea = false;
        //Read file and update UI
        if(list!=null){
            list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));
            mAdapter.setNewData(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_list);
        IsEnterPrivateArea = false;
        //get permission
        RxPermissions.getInstance(DialogListActivity.this)
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

        //get host info
        SharedPreferences sp = getSharedPreferences("USERINFO",0);
        String host = sp.getString("PHONE_NUM","");

        //init fileHelper
        fileHelper = new FileHelper(this);

        //init recyclerview
        recyclerView = findViewById(R.id.msgs_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = fileHelper.getPreviewData(fileHelper.castPreview(fileHelper.ReadFromFile()));

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
                Intent intent = new Intent(DialogListActivity.this, ChatActivity.class);
                intent.putExtra("partner",list.get(position).getUsername());
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
                                fileHelper.DeleteByPartner(list.get(position).getUsername());
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
                Log.d("SCORO",i+"  "+i1);
            }
        });
        bounceScrollView.setOnOverScrollListener(new BounceScrollView.OnOverScrollListener() {
            @Override
            public void onOverScrolling(boolean b, int i) {
                Log.d("OVER",""+i);
                if(IsEnterPrivateArea==false&&i>300){
                    IsEnterPrivateArea = true;
                    startActivity(new Intent(DialogListActivity.this, checkFinger.class));
//                    finish();
                }
            }
        });

    }

}
