package com.whut.androidtest.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.whut.androidtest.bean.MsgDetailBean;
import com.whut.androidtest.R;

import java.util.List;

public class ChatListAdapter extends BaseQuickAdapter<MsgDetailBean, BaseViewHolder> {
    public ChatListAdapter(int layoutResId, @Nullable List<MsgDetailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgDetailBean item) {
        //不显示状态为-1的消息（等待云端删除）
        if(item.getState()!=-1){
            if(item.getType()==0){
                //隐藏右侧布局
                helper.setGone(R.id.left_area,true)
                        .setGone(R.id.right_area,false)
                        .setText(R.id.left_date, item.getDate())
                        .setText(R.id.left_msg, item.getContent())
                        .addOnLongClickListener(R.id.left_msg);

            }
            if(item.getType()==1){
                //隐藏左侧布局
                helper.setGone(R.id.left_area, false)
                        .setGone(R.id.right_area, true)
                        .setText(R.id.right_date, item.getDate())
                        .setText(R.id.right_msg, item.getContent())
                        .addOnLongClickListener(R.id.right_msg);
            }
        }
        else if(item.getState()==-1){
            helper.setGone(R.id.msg_detail_item, false);
        }
    }
}
