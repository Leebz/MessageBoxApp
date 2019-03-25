package com.whut.androidtest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.whut.androidtest.R;
import com.whut.androidtest.adapter.SwipeAdapter;
import com.whut.androidtest.adapter.recyclerAdapter;
import com.xw.repo.widget.BounceScrollView;

import java.util.ArrayList;

public class Swipe extends AppCompatActivity {
    private SwipeAdapter mAdapter;
    private BounceScrollView bounceScrollView;
    private recyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_swipe);
//        bounceScrollView = findViewById(R.id.bounceView);
//
//        bounceScrollView.setOnOverScrollListener(new BounceScrollView.OnOverScrollListener() {
//            @Override
//            public void onOverScrolling(boolean b, int i) {
//                Log.d("OVER",""+i);
//                if(b==true&&i==50){
//                    Toast.makeText(Swipe.this, "SSSSSSSSSSSSSSSSSS", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(Swipe.this, MsgPreviewActivity.class));
//                    finish();
//                }
//            }
//        });

        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            list.add("test"+i);
        }
//        ListView listView = findViewById(R.id.msgs_list);
//
//        mAdapter = new SwipeAdapter(this, list);
//        listView.setAdapter(mAdapter);
        RecyclerView recyclerView = findViewById(R.id.msgs_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter  = new recyclerAdapter(list, this);
        ((recyclerAdapter)adapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(adapter);


    }
}
