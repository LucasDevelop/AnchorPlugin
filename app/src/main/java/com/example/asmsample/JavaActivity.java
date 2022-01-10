package com.example.asmsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lucas.analytics.Analytics;
import com.lucas.analytics.annotation.TrackMethod;
import com.lucas.analytics.annotation.TrackPage;
import com.lucas.loginlib.LoginActivity;

@TrackPage(moduleName = "java 页面",pageName = "JavaActivity",childPageName = "JavaActivity")
public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Analytics.INSTANCE.trackPage("aa","bb","cc",JavaActivity.class);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, LoginActivity.class));
        register("qqqqqqq");
    }



    @TrackMethod(des = "用户注册",trackParams = {"name"})
    public void register(String name){
      String temp = "aaaa";
        Log.d("lucas",temp);
    }

}
