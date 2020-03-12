package com.hjds.hjdsrouterlib.util;

import com.hjds.jrouterannotation.JRouterProvider;

/**
 * @author zhangxiaowei 2020-03-12
 */
class RouterUtil {
    private static JRouterProvider jRouterProvider;

    public static JRouterProvider getInstance() {
        if (jRouterProvider == null) {
            synchronized (RouterUtil.class) {
                if (jRouterProvider == null) {
                    try {
                        Class clazz = Class.forName("com.hjds.hjdsrouterlib.JRouterProviderImp");
                        if (clazz != null)
                            jRouterProvider = (JRouterProvider) clazz.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jRouterProvider;
    }
}
