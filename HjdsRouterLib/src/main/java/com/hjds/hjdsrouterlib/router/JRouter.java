package com.hjds.hjdsrouterlib.router;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.hjds.hjdsrouterlib.util.ActivityLifecycleHelper;

import java.io.Serializable;

import androidx.fragment.app.Fragment;

/**
 * @author zhangxiaowei 2020-03-07
 */
public class JRouter {
    String mRoutePath;
    Intent mIntent;
    private static boolean mDebug;

    public static boolean debug() {
        return mDebug;
    }

    public static void setmDebug(boolean mDebug) {
        JRouter.mDebug = mDebug;
    }

    private JRouter(String url) {
        mRoutePath = url;
        mIntent = new Intent();
    }

    public static JRouter build(String url) {
        return new JRouter(url);
    }


    public JRouter withSerializableObject(String key, Serializable value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JRouter withString(String key, String value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JRouter withBoolean(String key, boolean value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JRouter withLong(String key, long value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JRouter withInt(String key, int value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JRouter withDouble(String key, double value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public Object navigation() {
        try {
            Class clazz = RouterUtil.getInstance().get(mRoutePath);
            if (clazz == null) {
                if (JRouter.mDebug) Log.e("navigation", " acturl= " + mRoutePath + " clazz=null ");
                return null;
            }
//                Class clazz = Class.forName(acturl);
            if (JRouter.mDebug)
                Log.e("navigation", " acturl= " + mRoutePath + " clazz= " + clazz.getName());
            if (Fragment.class.isAssignableFrom(clazz)) {
                Fragment fragment = (Fragment) clazz.newInstance();
                fragment.setArguments(mIntent.getExtras());
                return fragment;
            } else if (Activity.class.isAssignableFrom(clazz)) {
                mIntent.setClass(ActivityLifecycleHelper.getLatestActivity(), clazz);
                ActivityLifecycleHelper.getLatestActivity().startActivity(mIntent);
            } else {
                return clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object navigation(Activity context, int requestCode) {
        try {
            Class clazz = RouterUtil.getInstance().get(mRoutePath);
            if (clazz == null) {
                if (JRouter.mDebug) Log.e("navigation", " acturl= " + mRoutePath + " clazz=null ");
                return null;
            }
            if (Fragment.class.isAssignableFrom(clazz)) {
                Fragment fragment = (Fragment) clazz.newInstance();
                fragment.setArguments(mIntent.getExtras());
                return fragment;
            } else if (Activity.class.isAssignableFrom(clazz)) {
                mIntent.setClass(context, clazz);
                if (mIntent != null) context.startActivityForResult(mIntent, requestCode);
            } else {
                return clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
