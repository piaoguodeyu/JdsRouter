package com.hjds.loginlib;

import android.app.Activity;
import android.os.Bundle;

import com.hjds.jrouterannotation.JRouter;

import androidx.annotation.Nullable;

/**
 * @author zhangxiaowei 2020-03-08
 */
@JRouter(path = "LoginActivity")
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
