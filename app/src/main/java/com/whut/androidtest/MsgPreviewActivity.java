package com.whut.androidtest;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.whut.androidtest.pojo.MsgPreviewPojo;

import java.util.ArrayList;
import java.util.List;

public class MsgPreviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton btnAddMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_preview);
        recyclerView = (RecyclerView)findViewById(R.id.msg_preview_list);

        recyclerView.setHasFixedSize(true);
        //use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MsgPreviewPojo> list = new ArrayList<>();
        MsgPreviewPojo a = new MsgPreviewPojo();
        a.setUsername("sss");
        a.setContent("123");
        a.setDate("2019");
        list.add(a);
//
//        mAdapter = new QuickAdapter(R.id.item_preview, list);
//        recyclerView.setAdapter(mAdapter);

        //specify an adapter
        String[] myDataset={"111","222","333","111","222","333"};
        String[] dateSet = {"2019-01-05","2019-09-01","2019-11-22","2019-01-05","2019-09-01","2019-11-22"};
        String[] content = {"content1","content2","content3","content1","content2","content3"};
        mAdapter = new PreviewListAdpter(myDataset,dateSet,content);
        recyclerView.setAdapter(mAdapter);

        btnAddMsg = (FloatingActionButton)findViewById(R.id.fab);
        btnAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MsgPreviewActivity.this, EditMsgActivity.class);
                startActivity(intent);
            }
        });


    }
}
