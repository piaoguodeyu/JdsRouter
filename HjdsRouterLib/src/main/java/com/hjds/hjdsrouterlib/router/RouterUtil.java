package com.hjds.hjdsrouterlib.router;

import android.util.Log;

import com.hjds.hjdsrouterlib.util.ActivityLifecycleHelper;
import com.hjds.jrouterannotation.RouterProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * @author zhangxiaowei 2020-03-12
 */
class RouterUtil {
    private static Map<String, Class> mMap;
    static long time;

    public static Map<String, Class> getInstance() {
        if (mMap == null) {
            synchronized (RouterUtil.class) {
                if (mMap == null) {
                    try {
                        time = System.currentTimeMillis();
                        String packagePath = "com.hjds.routerlibs";
                        try {
                            // 反射调用Init类，避免引用的类过多，导致main dex capacity exceeded问题
                            Class.forName(packagePath + ".JdsRouterUtil")
                                    .getMethod("init")
                                    .invoke(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            List<String> list = getClassName(packagePath);
                            for (String clazz : list) {
                                try {
                                    Class.forName(clazz);
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            }
                        }
                        time = System.currentTimeMillis() - time;
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
            try {
                String packagePath = packageName.replace(".", "/");
                PathClassLoader pathClassLoader1 = new PathClassLoader(packageName, RouterUtil.class.getClassLoader());
                Enumeration<URL> url = pathClassLoader1.getResources(packagePath);
                Log.e("pathClassLoader", "5555 " + url.hasMoreElements() + " " + packagePath);
                while (url.hasMoreElements()) {
                    Log.e("pathClassLoader", "66666666666666666");
                }
                URL url1 = pathClassLoader1.getResource(packagePath);
                Log.e("pathClassLoader", "url1" + url1.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }


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

    /**
     * 扫描包路径下所有的class文件
     *
     * @param pkg
     * @return
     */
    public static Set<Class<?>> getClzFromPkg(String pkg) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        String pkgDirName = pkg.replace('.', '/');
        try {
            PathClassLoader loader2222222 = (PathClassLoader) Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = loader2222222.getResources(pkgDirName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Log.e("pathClassLoader", "url1" + url.toString());
                String protocol = url.getProtocol();
//                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
//                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
//                    findClassesByFile(pkg, filePath, classes);
//                } else if ("jar".equals(protocol)) {// 如果是jar包文件
//                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
//                    findClassesByJar(pkg, jar, classes);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }
}
