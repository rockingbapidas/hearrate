package com.vantagecircle.heartrate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bapidas on 10/07/17.
 */

public class ToolsUtils {
    private static ToolsUtils mInstance;

    public static ToolsUtils getInstance(){
        if (mInstance == null) {
            synchronized (ToolsUtils.class) {
                if (mInstance == null) {
                    mInstance = new ToolsUtils();
                }
            }
        }
        return mInstance;
    }

    public boolean isHasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getTime(long timestamp) {
        SimpleDateFormat newformat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return newformat.format(date);
    }

    public String getDate(long timestamp) {
        SimpleDateFormat newformat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return newformat.format(date);
    }
}
