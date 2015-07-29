package com.genglun.listview;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by garytan on 15/7/29.
 */
public class DBAccess  {
    static SQLiteOpenHelper dbhelper;
    static DBAccess instance;
    static void init(DBOpenHelper helper){
        if(instance==null){
            instance=new DBAccess();
            dbhelper=helper;
        }

    }
    public static DBAccess getInstance(){
        if(instance==null){
            throw new IllegalStateException("creat instance not yet");
        }
        return instance;
    }
    public long insert(String table, ContentValues values)
    {
        SQLiteDatabase db=getDatabase();
        long id=db.insert(table,null,values);
        return id;
    }
    public Cursor query(String table,String whereClause, String orderBy){
        SQLiteDatabase db=getDatabase();
        Cursor c=db.query(table,null,whereClause, null, null, null, orderBy);
        return c;
    }
    public int delete(String table, String selection)
    {
        SQLiteDatabase db=getDatabase();
        int rowsDeleted = db.delete(table, selection, null);
        return rowsDeleted;
    }
    public int update(String table, ContentValues values, String selection) {
        SQLiteDatabase db=getDatabase();
        int rowsUpdated = db.update(table,values,selection,null);
        return rowsUpdated;
    }
    public SQLiteDatabase getDatabase()
    {
        return dbhelper.getWritableDatabase();
    }

}
