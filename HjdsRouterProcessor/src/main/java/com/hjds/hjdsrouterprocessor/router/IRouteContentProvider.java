package com.hjds.hjdsrouterprocessor.router;

import java.util.Map;

/**
 * 功能：
 * 描述：
 * Created by 陈俊杰 on 2018/7/19.
 */
public interface IRouteContentProvider {
    void handleRoute(Map<String, Class<?>> map);
}
