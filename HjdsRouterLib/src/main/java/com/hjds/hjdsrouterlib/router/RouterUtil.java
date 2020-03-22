package com.hjds.hjdsrouterlib.router;

import android.util.Log;

import com.hjds.hjdsrouterlib.util.ActivityLifecycleHelper;
import com.hjds.jrouterannotation.RouterProvider;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * @author zhangxiaowei 2020-03-12
 */
class RouterUtil {
    private static Map<String, Class> mMap;

    public static Map<String, Class> getInstance() {
        if (mMap == null) {
            synchronized (RouterUtil.class) {
                if (mMap == null) {
                    try {
                        List<String> list = getClassName("com.hjds.routerlibs");
                        for (String clazz : list) {
                            try {
                                if (JRouter.debug())
                                    Log.e("clazzclazzclazz", " 2222222= " + clazz);
                                Class.forName(clazz);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (JRouter.debug())
                            Log.e("clazzclazzclazz", " 3333333333= " + list.size());
                        mMap = RouterProvider.mMap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mMap;
    }

    /**
     * 获取包下的所有类
     *
     * @param packageName
     * @return
     */
    public static List<String> getClassName(String packageName) {
        List<String> classNameList = new ArrayList<>();
        try {
            String path = ActivityLifecycleHelper.getApplication()
                    .getPackageCodePath();
            DexFile df = new DexFile(path);//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();
                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNameList;
    }
}
