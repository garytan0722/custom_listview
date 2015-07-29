package com.genglun.listview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG="MainActivty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listview=(ListView)findViewById(R.id.listView);
        String from[]={"data","time","button"};
        int to[]={R.id.textView,R.id.textView2,R.id.imageButton};
        ArrayList<HashMap<String,String>> list=new ArrayList<>();
        String date="2015-07-29";
        for(int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put(from[0],date);
            map.put(from[1],i+"");
            list.add(map);
        }
        MyAdapter adapter=new MyAdapter(this,list,R.layout.list,from,to);//丟資料進去adapter
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//載入xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.start)
            Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show();
        else if(id==R.id.stop)
            Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this,"Item:"+i,Toast.LENGTH_SHORT).show();
    }
}
