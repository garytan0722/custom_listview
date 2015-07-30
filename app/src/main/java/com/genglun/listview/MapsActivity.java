package com.genglun.listview;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    String id;
    String projection[]={DBOpenHelper.RECORD_LAT,DBOpenHelper.RECORD_LON};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        id=getIntent().getStringExtra("id");
        Log.d("mylog","id:::"+id);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(this, MyContentProvider.RECORD_URI, projection,DBOpenHelper.RECORD_REFID+"="+id, null, DBOpenHelper.RECORD_DATE+" asc,"+DBOpenHelper.TRACKER_TIME+" asc");//更新資料

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {//取資料之後更新ui
        if(c!=null&&c.getCount()>0)
        {
            c.moveToFirst();
            mMap.clear();
            PolylineOptions options=new PolylineOptions();
            while (!c.isAfterLast()) {
                double lat = c.getDouble(0);
                double lon = c.getDouble(1);
                LatLng l = new LatLng(lat, lon);
                MarkerOptions s=new MarkerOptions();
                s.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                s.position(l);
                options.add(l);
                mMap.addMarker(s);
                //Log.i("mylog", lat + "," + lon);

                CameraUpdate update = CameraUpdateFactory.newLatLng(l);
                mMap.moveCamera(update);
                c.moveToNext();
            }
            mMap.addPolyline(options);
            /*
            mMap.clear();
            PolylineOptions options=new PolylineOptions();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                //Log.i("mylog", c.getDouble(0) + "," + c.getDouble(1));
                double lat = c.getDouble(0);
                double lon = c.getDouble(1);
                //Log.i("mylog", lat + "," + lon);
                LatLng l = new LatLng(lat, lon);
                options.add(l);
                mMap.addMarker(new MarkerOptions().position(l).title("Marker"));
                CameraUpdate update = CameraUpdateFactory.newLatLng(l);
                mMap.moveCamera(update);
                c.moveToNext();

            }
            mMap.addPolyline(options);
*/
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
