package com.funlib.utily;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ManifestUtil {
    
    private static Object readMetaDataKey(Context context, String keyName) {
        try {
            ApplicationInfo appi =
                context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);
            return value;
        } catch (NameNotFoundException e) {
            return null;
        }
    }
    
    public static int getInt(Context context, String keyName) {
        return (Integer)readMetaDataKey(context, keyName);
    }
    
    public static String getString(Context context, String keyName) {
        return (String)readMetaDataKey(context, keyName);
    }
    
    public static Boolean getBoolean(Context context, String keyName) {
        return (Boolean)readMetaDataKey(context, keyName);
    }
    
    public static Object get(Context context, String keyName) {
        return readMetaDataKey(context, keyName);
    }
}
