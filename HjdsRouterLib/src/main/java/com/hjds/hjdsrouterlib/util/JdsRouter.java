package com.hjds.hjdsrouterlib.util;

import android.app.Activity;
import android.content.Intent;

import com.hjds.jrouterannotation.JRouterProvider;

import java.io.Serializable;

import androidx.fragment.app.Fragment;

/**
 * @author zhangxiaowei 2020-03-07
 */
public class JdsRouter {
    static JRouterProvider jRouterProvider;
    String mRoutePath;
    Intent mIntent;

    private JdsRouter(String url) {
        mRoutePath = url;
        if (jRouterProvider == null) {
            try {
                Class clazz = Class.forName("com.hjds.hjdsrouterlib.JRouterProviderImp");
                if (clazz != null)
                    jRouterProvider = (JRouterProvider) clazz.newInstance();
                mIntent = new Intent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mIntent = new Intent();
        }
    }

    public static JdsRouter build(String url) {
        return new JdsRouter(url);
    }


    public JdsRouter withSerializableObject(String key, Serializable value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JdsRouter withString(String key, String value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JdsRouter withBoolean(String key, boolean value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JdsRouter withLong(String key, long value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JdsRouter withInt(String key, int value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public JdsRouter withDouble(String key, double value) {
        if (mIntent != null) mIntent.putExtra(key, value);
        return this;
    }

    public Object navigation() {
        if (mIntent != null) {
            try {
                String acturl = (String) jRouterProvider.getAllRouter().get(mRoutePath);
                Class clazz = Class.forName(acturl);
                if (Fragment.class.isAnnotationPresent(clazz)) {
                    Fragment fragment = (Fragment) clazz.newInstance();
                    fragment.setArguments(mIntent.getExtras());
                    return fragment;
                }
                mIntent.setClass(ActivityLifecycleHelper.getLatestActivity(), clazz);
                ActivityLifecycleHelper.getLatestActivity().startActivity(mIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void navigation(Activity mContext, int requestCode) {
        if (mIntent != null) mContext.startActivityForResult(mIntent, requestCode);
    }

}
