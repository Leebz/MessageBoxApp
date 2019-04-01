package com.whut.androidtest.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.whut.androidtest.R;
import com.whut.androidtest.bean.MsgPreviewBean;

import java.util.List;

public class DialogListAdapter extends BaseQuickAdapter<MsgPreviewBean, BaseViewHolder> {

    public DialogListAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgPreviewBean item) {
        helper.setText(R.id.msg_item_name,item.getUsername())
                .setText(R.id.msg_item_time,item.getDate())
                .setText(R.id.msg_item_preview,item.getContent());
    }
}
