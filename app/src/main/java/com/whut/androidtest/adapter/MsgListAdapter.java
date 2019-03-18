package com.whut.androidtest.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.whut.androidtest.Bean.MsgDetailBean;
import com.whut.androidtest.R;

import java.util.List;

public class MsgListAdapter extends BaseQuickAdapter<MsgDetailBean, BaseViewHolder> {
    public MsgListAdapter(int layoutResId, @Nullable List<MsgDetailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgDetailBean item) {
        if(item.getType()==0){
            //隐藏右侧布局
            helper.setGone(R.id.left_area,true)
                    .setGone(R.id.right_area,false)
                    .setText(R.id.left_date, item.getDate())
                    .setText(R.id.left_msg, item.getContent());

        }
        if(item.getType()==1){
            //隐藏左侧布局
            helper.setGone(R.id.left_area, false)
                    .setGone(R.id.right_area, true)
                    .setText(R.id.right_date, item.getDate())
                    .setText(R.id.right_msg, item.getContent());
        }

    }
}
