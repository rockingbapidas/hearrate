package com.vantagecircle.heartrate.data;

/**
 * Created by SiD on 11/12/2017.
 */

public class DataModel {
    private static final String TEXT_TYPE = " VARCHAR";
    private static final String COMMA_SEP = ", ";

    static final String HISTORY_TABLE = "history_table";
    static final String COLUMN_ID = "id";
    static final String COLUMN_HEART_RATE = "heart_rate";
    static final String COLUMN_DATE_STRING = "date_string";
    static final String COLUMN_TIME_STRING = "time_string";

    static final String CREATE_HEART_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    COLUMN_HEART_RATE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_DATE_STRING + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TIME_STRING + TEXT_TYPE +
                    ")";
    static final String GET_HISTORY_QUERY = "SELECT * FROM " + HISTORY_TABLE;
}
