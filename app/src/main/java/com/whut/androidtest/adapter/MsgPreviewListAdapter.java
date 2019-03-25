package com.whut.androidtest.adapter;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.whut.androidtest.R;
import com.whut.androidtest.bean.MsgPreviewBean;

import java.util.List;

public class MsgPreviewListAdapter extends BaseItemDraggableAdapter<MsgPreviewBean, BaseViewHolder> {
    public MsgPreviewListAdapter(int layoutResId, List<MsgPreviewBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgPreviewBean item) {
        helper.setText(R.id.msg_item_name,item.getUsername())
                .setText(R.id.msg_item_time,item.getDate())
                .setText(R.id.msg_item_preview,item.getContent());


    }
}
