package com.example.owner.changeconfigurationtest3;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermision();
        changeLocaleConfiguration();
    }


    private void setPermision() {
        Process p = null;
        try {
            p=Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream dos = new DataOutputStream(p.getOutputStream());
        try {
            dos.writeBytes("adb shell\n");
            dos.writeBytes("pm grant com.example.owner.changeconfigurationtest3 android.permission.CHANGE_CONFIGURATION\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
        } catch (Exception  e) {
            e.printStackTrace();
        }finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                p.destroy();
            } catch (Exception e) {
            }
        }


    }


    private void changeLocaleConfiguration() {
        Locale locale = new Locale("en");

        Class amnClass = null;
        try {
            amnClass = Class.forName("android.app.ActivityManagerNative");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object amn = null;
        Configuration config = null;

        // amn = ActivityManagerNative.getDefault();
        Method methodGetDefault = null;
        try {
            methodGetDefault = amnClass.getMethod("getDefault");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        methodGetDefault.setAccessible(true);
        try {
            amn = methodGetDefault.invoke(amnClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // config = amn.getConfiguration();
        Method methodGetConfiguration = null;
        try {
            methodGetConfiguration = amnClass.getMethod("getConfiguration");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        methodGetConfiguration.setAccessible(true);
        try {
            config = (Configuration) methodGetConfiguration.invoke(amn);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // config.userSetLocale = true;
        Class configClass = config.getClass();
        Field f = null;
        try {
            f = configClass.getField("userSetLocale");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            f.setBoolean(config, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // set the locale to the new value
        config.locale = locale;

        // amn.updateConfiguration(config);
        Method methodUpdateConfiguration = null;
        try {
            methodUpdateConfiguration = amnClass.getMethod("updateConfiguration", Configuration.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        methodUpdateConfiguration.setAccessible(true);
        try {
            methodUpdateConfiguration.invoke(amn, config);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
