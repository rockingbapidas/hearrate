package com.bapidas.heartrate.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by bapidas on 10/07/17.
 */
object ToolsUtils {

    fun isHasPermissions(
        context: Context,
        vararg permissions: String
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    fun getTime(timestamp: Long): String {
        val newFormat =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val date = Date(timestamp)
        return newFormat.format(date)
    }

    fun getDate(timestamp: Long): String {
        val newFormat =
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date = Date(timestamp)
        return newFormat.format(date)
    }
}