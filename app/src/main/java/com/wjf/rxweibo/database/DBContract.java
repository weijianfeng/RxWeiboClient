package com.wjf.rxweibo.database;

/**
 * description
 *
 * @author weijianfeng
 * @date 16/8/22
 */
public class DBContract {

    public interface User {

    }

    public interface Status {
        public static final String TABLE_NAME = "status";
        public static final String COLUMN_NAME_ID = "status_id";
        public static final String COLUMN_NAME_STATUS_TEXT = "status_text";
    }
}
