package com.whut.androidtest;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.whut.androidtest.pojo.MsgPreviewPojo;

import java.util.List;

public class QuickAdapter extends BaseQuickAdapter<MsgPreviewPojo, BaseViewHolder> {
    public QuickAdapter(int layoutResId, List<MsgPreviewPojo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgPreviewPojo item) {
        helper.setText(R.id.msg_item_name,item.getUsername());


    }
}
