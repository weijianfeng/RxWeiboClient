package com.wjf.rxweibo.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.Nullable;

import com.wjf.rxweibo.database.DBHelper;

import java.util.List;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/8/22
 */
public class BaseDao {

    protected Context mContext;
    protected DBHelper mDbHelper;

    public BaseDao(Context context) {
        mContext = context.getApplicationContext();
        mDbHelper = new DBHelper(mContext);
        mDbHelper.getWritableDatabase();
    }

    protected Cursor query(String tableName, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);
        return cursor;
    }

    /**
     *
     * @param tableName
     * @param projectionIn 需要返回的字段，null返回所有字段
     * @param selection where语句, 不包含where关键字，eg id=? and age=?
     * @param selectionArgs 对应where语句?的值 new String[]{String.valueOf(id), String.valueOf(age)}
     * @param groupBy
     * @param having
     * @param sortOrder 升序降序 id desc
     * @param limit 返回结果的数目
     * @return
     */
    protected Cursor query(String tableName, String[] projectionIn,
                           String selection, String[] selectionArgs, String groupBy,
                           String having, String sortOrder, String limit) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        Cursor cursor = builder.query(mDbHelper.getReadableDatabase(),
                projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit
        );

        return cursor;

    }


    protected Cursor rawQuery(String sql, String[] args) {
        Cursor cursor = mDbHelper.getReadableDatabase().rawQuery(sql, args);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    protected long insert(String tableName, ContentValues cv) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.insert(tableName, null, cv);
    }

    protected void batchInsert(String tableName, List<ContentValues> cvs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues cv : cvs) {
                db.insert(tableName, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    protected void delete(String tableName) {
        this.delete(tableName, null, null);
    }

    public int delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int deleted = db.delete(tableName, whereClause, whereArgs);
        return deleted;
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.update(table, values, whereClause, whereArgs);
    }


    public static String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public static double getDouble(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    public static boolean getBoolean(Cursor cursor, String column) {
        return getInt(cursor, column) == 0 ? false : true;
    }
}
