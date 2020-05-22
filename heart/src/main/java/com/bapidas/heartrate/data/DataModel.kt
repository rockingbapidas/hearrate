package com.bapidas.heartrate.data

/**
 * Created by SiD on 11/12/2017.
 */
internal object DataModel {
    private const val TEXT_TYPE = " VARCHAR"
    private const val COMMA_SEP = ", "
    const val HISTORY_TABLE = "history_table"
    const val COLUMN_ID = "id"
    const val COLUMN_HEART_RATE = "heart_rate"
    const val COLUMN_DATE_STRING = "date_string"
    const val COLUMN_TIME_STRING = "time_string"
    const val CREATE_HEART_TABLE_QUERY =
        "CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                COLUMN_HEART_RATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_DATE_STRING + TEXT_TYPE + COMMA_SEP +
                COLUMN_TIME_STRING + TEXT_TYPE +
                ")"
    const val GET_HISTORY_QUERY = "SELECT * FROM $HISTORY_TABLE"
}