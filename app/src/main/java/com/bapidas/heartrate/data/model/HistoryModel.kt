package com.bapidas.heartrate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by SiD on 11/11/2017.
 */
@Entity(tableName = "history_table")
data class HistoryModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val heartRate: String,
    val dateString: String,
    val timeString: String
)