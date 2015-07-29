package com.genglun.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by garytan on 15/7/29.
 */
//resource是list的畫面
//from是項目對應的key
//to是元件的id
public class MyAdapter extends BaseAdapter implements View.OnClickListener {
    public Context context;
    public ArrayList<HashMap<String,String>> list;
    public String from[];
    public int to[];
    public int resource;
    public MyAdapter(Context context,ArrayList<HashMap<String,String>> list,int resource,String from[],int to[]){
        this.context=context;
        this.list=list;
        this.from=from;
        this.to=to;
        this.resource=resource;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//顯示畫面
        View v;
        ViewHolder holder;
        if(view==null){//若項目沒有畫面時必須產生畫面
            holder=new ViewHolder();
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            v=layoutInflater.inflate(resource,null);//layout xml檔 轉化成物件
            holder.v1=(TextView)v.findViewById(to[0]);
            holder.v2=(TextView)v.findViewById(to[1]);
            holder.imgbutn=(ImageButton)v.findViewById(to[2]);
            v.setTag(holder);//暫存物件
        }
        else{
            v=view;
            holder=(ViewHolder)view.getTag();
        }
        holder.v1.setText(list.get(i).get(from[0]));
        holder.v2.setText(list.get(i).get(from[1]));
        holder.imgbutn.setTag(i);
        holder.imgbutn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
            int i=(Integer)view.getTag();//回傳按到的項目位置
            list.remove(i);
            this.notifyDataSetChanged();//通知資料要更新畫面要重做

    }

    static class ViewHolder{//暫存class
        TextView v1;
        TextView v2;
        ImageButton imgbutn;
    }
}
