package com.example.order.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.amap.api.services.help.Tip;
import com.example.order.R;


import java.util.List;


public class RvAdapter extends RecyclerView.Adapter <RvAdapter.MyViewHolder> {
    private Context context;
    private RecyclerView rv;
    public List<Tip> list;
    //声明自定义的监听接口
    private OnRecyclerItemClickListener monItemClickListener;

    //提供set方法供Activity或Fragment调用
    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        monItemClickListener=listener;
    }


    public RvAdapter(Context context, RecyclerView view, List<Tip> list){
        this.context = context;
        this.rv = view;
        this.list =list;
    }

    public void setData(List<Tip> list){
        if(list == null){
            return;
        }
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tip tip = list.get(position);
        holder.address.setText(tip.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monItemClickListener!=null){

                    monItemClickListener.onItemClick(position,list);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView address;
        public MyViewHolder(@NonNull View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);

        }

    }
}
