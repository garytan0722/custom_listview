package com.genglun.listview;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private final String TAG="MainActivty";
    public DBOpenHelper helper;
    public DBAccess dbaccess;
    public CursorAdapter adapter;
    String projection[]={DBOpenHelper.TRACKER_ID,DBOpenHelper.TRACKER_DATE,DBOpenHelper.TRACKER_TIME};
    GoogleApiClient apiClient;
    LocationRequest request;
    String id;
    MenuItem startMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listview=(ListView)findViewById(R.id.listView);
        int to[]={R.id.textView,R.id.textView2,R.id.imageButton};
        String from[]={"data","time","button"};
        String projection[]={DBOpenHelper.TRACKER_ID,DBOpenHelper.TRACKER_DATE,DBOpenHelper.TRACKER_TIME};
        /*
        ArrayList<HashMap<String,String>> list=new ArrayList<>();
        String date="2015-07-29";
        for(int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put(from[0],date);
            map.put(from[1],i+"");
            list.add(map);
        }
        MyAdapter adapter=new MyAdapter(this,list,R.layout.list,from,to);//丟資料進去adapter
        */
        //從資料庫利用cursor來抓取資料到ui透過loadmanger
        Cursor c=getContentResolver().query(MyContentProvider.TRACKER_URI,projection,null,null,null);
        adapter=new MyCursorAdapter(this,null,0);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        getLoaderManager().initLoader(11,null,this);//更新ui用loadmanager

        //SQLite
        /*helper=new DBOpenHelper(this,"test.db",null,1);
        DBAccess.init(helper);
        dbaccess=DBAccess.getInstance();
        ContentValues values=new ContentValues();
        values.put(DBOpenHelper.TRACKER_DATE, "2015-07-30");
        values.put(DBOpenHelper.TRACKER_TIME, "10:00");
        dbaccess.insert(DBOpenHelper.TRACKER_TABLE,values);*/

        //Content provider
        /*ContentValues values=new ContentValues();
        values.put(DBOpenHelper.TRACKER_DATE,"2015-07-31");
        values.put(DBOpenHelper.TRACKER_TIME,"10:00");
        getContentResolver().insert(MyContentProvider.TRACKER_URI,values);
        values=new ContentValues();
        values.put(DBOpenHelper.TRACKER_DATE,"2015-08-01");
        values.put(DBOpenHelper.TRACKER_TIME,"12:00");
        getContentResolver().insert(MyContentProvider.TRACKER_URI,values);*/

        //google map
        GoogleApiClient.Builder builder=new GoogleApiClient.Builder(this);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        builder.addApi(LocationServices.API);
        apiClient=builder.build();
        apiClient.connect();
        request=new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//載入xml
        startMenuItem=menu.findItem(R.id.start);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int _id = item.getItemId();
        if(_id==R.id.start)
            Toast.makeText(this,"Start",Toast.LENGTH_SHORT).show();
        if (apiClient.isConnected()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request,this);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.TRACKER_DATE, date);
            values.put(DBOpenHelper.TRACKER_TIME, time);
            Uri uri = getContentResolver().insert(MyContentProvider.TRACKER_URI, values);
            if (uri != null)
                id = uri.getLastPathSegment();
            item.setEnabled(false);
        }
        else
        {
            Toast.makeText(this,"GooglePlay Service未連接",Toast.LENGTH_LONG).show();

        }
        if(_id==R.id.stop)
            Toast.makeText(this, "停止", Toast.LENGTH_SHORT).show();
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient,this);
            startMenuItem.setEnabled(true);
        }
        id="";

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//i 是第幾項 l是獨有的id
        Intent intent=new Intent();
        intent.setClass(this,MapsActivity.class);
        intent.putExtra("id",l+"");
        startActivity(intent);
        Toast.makeText(this,"Item:"+i,Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {//更新資料
        CursorLoader cursorLoader = new CursorLoader(this, MyContentProvider.TRACKER_URI, projection, null, null, null);//更新資料
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {//取資料之後更新ui
        if (adapter != null && cursor != null)
            adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null)
            adapter.swapCursor(null);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this,"onConnected",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), MainActivity.this, 2000).show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!id.equals("")) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));

            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.RECORD_LAT, location.getLatitude() + "");
            values.put(DBOpenHelper.RECORD_LON, location.getLongitude() + "");
            values.put(DBOpenHelper.RECORD_DATE, date);
            values.put(DBOpenHelper.RECORD_TIME, time);
            values.put(DBOpenHelper.RECORD_REFID, id);
            getContentResolver().insert(MyContentProvider.RECORD_URI, values);
        }
        Log.i("mylog", location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
