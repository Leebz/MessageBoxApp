package com.whut.androidtest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.whut.androidtest.R;

import java.util.ArrayList;

public class SwipeAdapter extends BaseSwipeAdapter{
    private Context context;
    private ArrayList<String> list;
    private LinearLayout surfaceArea;

    public SwipeAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.msg_item2, null);

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView textView = (TextView)convertView.findViewById(R.id.text);
        textView.setText(list.get(position));
        SwipeLayout swipeLayout = convertView.findViewById(R.id.swipe);
        swipeLayout.addSwipeListener(new SimpleSwipeListener(){

        });
        swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click"+position, Toast.LENGTH_SHORT).show();
            }
        });
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "DELETE"+position, Toast.LENGTH_SHORT).show();
            }
        });

        surfaceArea = convertView.findViewById(R.id.surface_area);
        surfaceArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context,)
            }
        });

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
