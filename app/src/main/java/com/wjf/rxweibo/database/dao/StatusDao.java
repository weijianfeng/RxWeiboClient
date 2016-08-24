package com.wjf.rxweibo.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wjf.rxweibo.database.DBContract;
import com.wjf.rxweibo.model.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/8/22
 */
public class StatusDao extends BaseDao{

    public StatusDao(Context context) {
        super(context);
    }

    synchronized public static void creatStatusTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + DBContract.Status.TABLE_NAME + " ("
                + DBContract.Status.COLUMN_NAME_ID + " INTEGER" + " PRIMARY KEY" + ","
                + DBContract.Status.COLUMN_NAME_STATUS_TEXT + " TEXT" +
                "); ";
        db.execSQL(sql);
    }

    public void saveStatuses(List<Status> statusList) {
        if (statusList == null) {
            return;
        }
        List<ContentValues> cvs = new ArrayList<>();
        for (Status s : statusList) {
            ContentValues cv = new ContentValues();
            cv.put(DBContract.Status.COLUMN_NAME_ID, s.id);
            cv.put(DBContract.Status.COLUMN_NAME_STATUS_TEXT, s.text.toString());
            cvs.add(cv);
        }
        batchInsert(DBContract.Status.TABLE_NAME, cvs);
    }

    public List<Status> getStatus(String lastId, int limit) {
        if (lastId.equals("0")) {
            return getLatestStatus(limit);
        } else {
            return getStatusByLastId(lastId, limit);
        }
    }

    public List<Status> getLatestStatus(int limit) {
        List<Status> statuses = new ArrayList<>();
        Cursor cursor = query(DBContract.Status.TABLE_NAME, null, null,
                null, null, null,DBContract.Status.COLUMN_NAME_ID + " DESC", String.valueOf(limit));
        if(cursor != null){
            try {
                while(cursor.moveToNext()){
                    Status status = new Status();
                    status.id = getString(cursor, DBContract.Status.COLUMN_NAME_ID);
                    status.text = getString(cursor, DBContract.Status.COLUMN_NAME_STATUS_TEXT);
                    statuses.add(status);
                }
            }finally {
                cursor.close();
            }
        }
        return statuses;
    }


    public List<Status> getStatusByLastId(String lastId, int limit) {
        List<Status> statuses = new ArrayList<>();
        Cursor cursor = query(DBContract.Status.TABLE_NAME, null, DBContract.Status.COLUMN_NAME_ID + "< ?",
                new String[]{String.valueOf(lastId)}, null, null, null, String.valueOf(limit));
        if(cursor != null){
            try {
                while(cursor.moveToNext()){
                    Status status = new Status();
                    status.id = getString(cursor, DBContract.Status.COLUMN_NAME_ID);
                    status.text = getString(cursor, DBContract.Status.COLUMN_NAME_STATUS_TEXT);
                    statuses.add(status);
                }
            }finally {
                cursor.close();
            }
        }
        return statuses;
    }


}
