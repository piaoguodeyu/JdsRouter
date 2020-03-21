package com.hjds.hjdsrouterlib.router;

import com.hjds.jrouterannotation.RouterProvider;

/**
 * @author zhangxiaowei 2020-03-12
 */
class RouterUtil {
    private static RouterProvider jRouterProvider;

    public static RouterProvider getInstance() {
        if (jRouterProvider == null) {
            synchronized (RouterUtil.class) {
                if (jRouterProvider == null) {
                    try {
                        Class clazz = Class.forName("com.hjds.hjdsrouterlib.RouterProviderImp");
                        if (clazz != null)
                            jRouterProvider = (RouterProvider) clazz.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jRouterProvider;
    }
}
