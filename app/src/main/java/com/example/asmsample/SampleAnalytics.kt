package com.example.asmsample

import android.app.Application
import android.util.Log
import com.lucas.analytics.common.IAnalytics

/**
 * File SampleAnalytics.kt
 * Date 2022/1/12
 * Author lucas
 * Introduction 埋点回调案例
 */
class SampleAnalytics : IAnalytics {

    override fun initConfig(context: Application) {
        Log.d("lucas", "初始化")
    }

    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
        Log.d("lucas", "绑定用户信息")
        Log.d("lucas", "uid:$uid")
        userProperty?.forEach {
            Log.d("lucas", "${it.key}:${it.value}")
        }
    }

    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
        Log.d("lucas", "【事件】eventName:$eventName,param:$param")
    }

    override fun trackPage(
        params: Map<String, Any>,
        methodName: String,
        des: String,
        pageClass: Class<*>
    ) {
        Log.d("lucas", "【页面】生命周期:${methodName},param:$params")
    }
}