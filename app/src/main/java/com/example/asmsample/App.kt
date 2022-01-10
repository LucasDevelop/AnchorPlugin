package com.example.asmsample

import android.app.Application
import com.lucas.analytics.Analytics

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        //新增代理
        Analytics.register(EventReportProxy())
    }
}