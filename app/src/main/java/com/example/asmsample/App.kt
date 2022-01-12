package com.example.asmsample

import android.app.Application
import com.lucas.analytics.Analytics

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        //注册监听
        Analytics.register(SampleAnalytics())
        //插件初始化
        Analytics.initConfig(this)
    }
}