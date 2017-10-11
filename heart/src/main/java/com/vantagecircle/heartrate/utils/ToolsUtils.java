package com.vantagecircle.heartrate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

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
}
