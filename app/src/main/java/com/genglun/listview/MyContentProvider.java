package com.genglun.listview;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

    private static final int TRACKER = 10;
    private static final int TRACKER_ID = 20;
    private static final int RECORD = 30;
    private static final int RECORD_ID = 40;
    private static final String AUTHORITY = "com.genglun.listview";

    private static final String TRACKER_BASE_PATH = "tracker";
    public static final Uri TRACKER_URI = Uri.parse("content://" + AUTHORITY + "/" + TRACKER_BASE_PATH);//定義在存取的資料庫路徑
    private static final String RECORD_BASE_PATH = "record";
    public static final Uri RECORD_URI = Uri.parse("content://" + AUTHORITY + "/" + RECORD_BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);//設定規則比對uri的位置若沒有對應就回傳-1
    static {
        sURIMatcher.addURI(AUTHORITY, TRACKER_BASE_PATH, TRACKER);
        sURIMatcher.addURI(AUTHORITY, TRACKER_BASE_PATH + "/#", TRACKER_ID);//＃號為任意數字
        sURIMatcher.addURI(AUTHORITY, RECORD_BASE_PATH, RECORD);
        sURIMatcher.addURI(AUTHORITY, RECORD_BASE_PATH + "/#", RECORD_ID);
    }

    DBOpenHelper helper;
    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db=helper.getWritableDatabase();
        int rowsDeleted = 0;
        String id;
        switch (uriType) {
            case TRACKER:
                rowsDeleted = db.delete(DBOpenHelper.TRACKER_TABLE,selection,selectionArgs);
                break;
            case TRACKER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DBOpenHelper.TRACKER_TABLE,
                            DBOpenHelper.TRACKER_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(DBOpenHelper.TRACKER_TABLE,
                            DBOpenHelper.TRACKER_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case RECORD:
                rowsDeleted = db.delete(DBOpenHelper.RECORD_TABLE, selection, selectionArgs);
                break;
            case RECORD_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DBOpenHelper.RECORD_TABLE,
                            DBOpenHelper.RECORD_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(DBOpenHelper.RECORD_TABLE,
                            DBOpenHelper.RECORD_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(rowsDeleted>0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case TRACKER:
                return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.tw.edu.ncu.ce.bnlab.tracker";
            case TRACKER_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.tw.edu.ncu.ce.bnlab.tracker";
            case RECORD:
                return ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.tw.edu.ncu.ce.bnlab.record";
            case RECORD_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.tw.edu.ncu.ce.bnlab.record";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db=helper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case TRACKER:
                id = db.insert(DBOpenHelper.TRACKER_TABLE,null,values);
                break;
            case RECORD:
                id = db.insert(DBOpenHelper.RECORD_TABLE,null,values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(id>0) {
            Uri resultUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(uri, null);

            return resultUri;
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean onCreate() {
        helper=new DBOpenHelper(getContext(),"test.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor;
        String id ;
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TRACKER:
                cursor=db.query(DBOpenHelper.TRACKER_TABLE,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case TRACKER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    cursor=db.query(DBOpenHelper.TRACKER_TABLE, projection, DBOpenHelper.TRACKER_ID + "=" + id,null,null,null,sortOrder);

                } else {
                    cursor=db.query(DBOpenHelper.TRACKER_TABLE, projection, DBOpenHelper.TRACKER_ID + "=" + id + " and " + selection, null, null, null, sortOrder);

                }

                break;
            case RECORD:
                cursor=db.query(DBOpenHelper.RECORD_TABLE,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case RECORD_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    cursor=db.query(DBOpenHelper.RECORD_TABLE, projection, DBOpenHelper.RECORD_ID + "=" + id,null,null,null,sortOrder);

                } else {
                    cursor=db.query(DBOpenHelper.RECORD_TABLE, projection, DBOpenHelper.RECORD_ID + "=" + id+ " and " + selection,null,null,null,sortOrder);

                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db=helper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        String id;
        int rowsUpdated = 0;
        switch (uriType) {
            case TRACKER:
                rowsUpdated = db.update(DBOpenHelper.TRACKER_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TRACKER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(DBOpenHelper.TRACKER_TABLE,
                            values,
                            DBOpenHelper.TRACKER_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(DBOpenHelper.TRACKER_TABLE,
                            values,
                            DBOpenHelper.TRACKER_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case RECORD:
                rowsUpdated = db.update(DBOpenHelper.RECORD_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case RECORD_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(DBOpenHelper.RECORD_TABLE,
                            values,
                            DBOpenHelper.RECORD_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(DBOpenHelper.RECORD_TABLE,
                            values,
                            DBOpenHelper.TRACKER_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(rowsUpdated>0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
