package com.genglun.listview;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;



/**
 * Created by garytan on 15/7/30.
 */
public class MyCursorAdapter extends CursorAdapter implements View.OnClickListener {
    private Context context;
    public MyCursorAdapter(Context context, Cursor c,int flag) {

        super(context, c);
        this.context=context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {//新增畫面
        ViewHolder holder=new ViewHolder();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View v=layoutInflater.inflate(R.layout.list, null);//layout xml檔 轉化成物件
        TextView v1=(TextView)v.findViewById(R.id.textView);
        TextView v2=(TextView)v.findViewById(R.id.textView2);
        ImageButton imgbutn=(ImageButton)v.findViewById(R.id.imageButton);
        holder.v1=v1;
        holder.v2=v2;
        holder.imgbutn=imgbutn;
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {//畫面已存在將資料填上去
        if(view==null||cursor==null)
            return;
        ViewHolder holder=(ViewHolder)view.getTag();;
        holder.v1.setText(cursor.getString(1));
        holder.v2.setText(cursor.getString(2));
        holder.imgbutn.setTag(cursor.getInt(0));
        holder.imgbutn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i=(int)view.getTag();
        context.getContentResolver().delete(MyContentProvider.TRACKER_URI,DBOpenHelper.TRACKER_ID+"="+i,null);
        context.getContentResolver().delete(MyContentProvider.RECORD_URI,DBOpenHelper.RECORD_REFID+"="+i,null);
        //this.notifyDataSetChanged();*/

    }

    static class ViewHolder{//暫存class
        TextView v1;
        TextView v2;
        ImageButton imgbutn;
    }
}
