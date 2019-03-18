package com.whut.androidtest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whut.androidtest.R;

public class PreviewListAdpter extends RecyclerView.Adapter<PreviewListAdpter.MyViewHolder> {
    private String[] mDataset;
    private String[] mDateSet;
    private String[] mContentSet;
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textName,textDate,textContent;
        public MyViewHolder(View v){
            super(v);
            textName = v.findViewById(R.id.msg_item_name);
            textDate = v.findViewById(R.id.msg_item_time);
            textContent = v.findViewById(R.id.msg_item_preview);
        }
    }
    public PreviewListAdpter(String[] myDataset,String[] dateSet, String[] contentSet){
        mDataset = myDataset;
        mDateSet = dateSet;
        mContentSet = contentSet;
    }
    @Override
    public PreviewListAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.textName.setText(mDataset[i]);
        myViewHolder.textDate.setText(mDateSet[i]);
        myViewHolder.textContent.setText(mContentSet[i]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
