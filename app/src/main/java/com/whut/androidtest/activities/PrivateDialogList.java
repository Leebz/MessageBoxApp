package com.whut.androidtest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.DialogListAdapter;
import com.whut.androidtest.bean.MsgPreviewBean;
import com.whut.androidtest.util.FileHelper;

import java.util.ArrayList;

public class PrivateDialogList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DialogListAdapter adapter;
    private ArrayList<MsgPreviewBean> list;
    private FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_private_dialog_list);

        fileHelper = new FileHelper(this);


        recyclerView = findViewById(R.id.private_dialog_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //init data
        list = fileHelper.getPreviewData(fileHelper.castPrivatePreview(fileHelper.ReadFromFile()));

        adapter = new DialogListAdapter(R.layout.msg_item, list);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String partner = list.get(position).getUsername();
                Intent intent = new Intent(PrivateDialogList.this, PrivateChatActivity.class);
                intent.putExtra("partner", partner);
                startActivity(intent);
            }
        });

    }
}
