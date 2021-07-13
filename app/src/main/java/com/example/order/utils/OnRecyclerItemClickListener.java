package com.example.order.utils;



import com.amap.api.services.help.Tip;

import java.util.List;

public interface OnRecyclerItemClickListener {
    //RecyclerView的点击事件，将信息回调给view
    void onItemClick(int Position, List<Tip> dataList);
}