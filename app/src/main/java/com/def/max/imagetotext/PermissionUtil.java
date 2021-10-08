package com.def.max.imagetotext;

import android.content.Context;
import android.content.SharedPreferences;

public class PermissionUtil
{
    private Context context;
    private SharedPreferences sharedPreferences;

    public static final String PERMISSION_CAMERA = "CAMERA";

    public static final int READ_CAMERA = 1;

    public static final int REQUEST_CAMERA = 2;

    public PermissionUtil(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference),Context.MODE_PRIVATE);
    }

    public void updatePermissionPreference(String permission)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (permission)
        {
            case PERMISSION_CAMERA:
                editor.putBoolean(context.getString(R.string.permission_CAMERA),true);
                editor.apply();
                break;
        }
    }

    public boolean checkPermissionPreference(String permission)
    {
        boolean isShown = false;

        switch (permission)
        {
            case PERMISSION_CAMERA:
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_CAMERA),false);
                break;
        }
        return !isShown;
    }
}