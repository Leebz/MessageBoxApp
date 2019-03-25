package com.whut.androidtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.whut.androidtest.activities.MsgPreviewActivity;
import com.xw.repo.widget.BounceScrollView;

public class BounceView extends AppCompatActivity {
    private BounceScrollView bounceScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounce);

        bounceScrollView = findViewById(R.id.bounceView);

        bounceScrollView.setOnOverScrollListener(new BounceScrollView.OnOverScrollListener() {
            @Override
            public void onOverScrolling(boolean b, int i) {
                Log.d("OVER",""+i);
                if(b==true&&i>500){
                    Toast.makeText(BounceView.this, "SSSSSSSSSSSSSSSSSS", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BounceView.this, MsgPreviewActivity.class));
                    finish();
                }
            }
        });


    }
}
