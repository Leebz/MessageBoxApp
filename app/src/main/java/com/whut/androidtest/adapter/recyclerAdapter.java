package com.whut.androidtest.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.whut.androidtest.R;
import com.whut.androidtest.activities.ChatActivity;
import com.whut.androidtest.activities.MsgsListActivity;
import com.whut.androidtest.bean.MsgPreviewBean;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerSwipeAdapter<recyclerAdapter.MyViewHolder>{
    private ArrayList<MsgPreviewBean> list;
    private Context context;

    public recyclerAdapter(ArrayList<MsgPreviewBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView msg_name;
        public TextView msg_time;
        public TextView msg_preview;
        public SwipeLayout swipeLayout;
        public LinearLayout surfaceArea;
        public Button btn_delete;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            msg_name = itemView.findViewById(R.id.msg_item_name);
            msg_time = itemView.findViewById(R.id.msg_item_time);
            msg_preview = itemView.findViewById(R.id.msg_item_preview);
            surfaceArea = itemView.findViewById(R.id.surface_area);
            btn_delete = itemView.findViewById(R.id.delete);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item2, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.msg_name.setText(list.get(position).getUsername());
        viewHolder.msg_time.setText(list.get(position).getDate());
        viewHolder.msg_preview.setText(list.get(position).getContent());
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener(){

        });
        viewHolder.surfaceArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("partner", list.get(position).getUsername());
                context.startActivity(intent);
            }
        });
        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
                normalDialog.setTitle("警告");
                normalDialog.setMessage("确定删除此消息吗?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "POSITION"+position, Toast.LENGTH_SHORT).show();
                                //...To-do
                                list.remove(position);
                                Log.d("SIZE",list.size()+"");


                            }
                        });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();



            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
