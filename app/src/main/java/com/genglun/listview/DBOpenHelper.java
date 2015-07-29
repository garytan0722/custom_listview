package com.genglun.listview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by garytan on 15/7/29.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    final static String TRACKER_TABLE="tracker";
    final static String TRACKER_ID="_id";
    final static String TRACKER_DATE="date";
    final static String TRACKER_TIME="time";

    final static String RECORD_TABLE="record";
    final static String RECORD_ID="_id";
    final static String RECORD_LAT="lat";
    final static String RECORD_LON="lon";
    final static String RECORD_DATE="date";
    final static String RECORD_TIME="time";
    final static String RECORD_REFID="refid";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_tracker="create table" +TRACKER_TABLE+"("
                +TRACKER_ID+"integer primary key autoinorement"
                +TRACKER_DATE+"text"
                +TRACKER_TIME+"text )";
        String create_record="create table" +RECORD_TABLE+"("
                +RECORD_ID+"integer primary key autoinorement"
                +RECORD_LAT+"text"
                +RECORD_LON+"text"
                +RECORD_LON+"text"
                +RECORD_DATE+"text"
                +RECORD_TIME+"text"
                +RECORD_REFID+"integer )";
        db.execSQL(create_record);
        db.execSQL(create_tracker);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("drop table if exists "+TRACKER_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+RECORD_TABLE);
        onCreate(sqLiteDatabase);
    }

}
