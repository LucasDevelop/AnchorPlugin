package com.lucas.analytics

import android.app.Application
import com.lucas.analytics.common.IAnalytics
import com.lucas.analytics.common.Lifecycle

/**
 * File Analytics.kt
 * Date 2021/6/3
 * Author lucas
 * Introduction 工具类
 */
object Analytics : IAnalytics {

    //代理对象
    private val proxyAnalytics = mutableListOf<IAnalytics>()

    //注册代理
    fun register(iAnalytics: IAnalytics) {
        if (!proxyAnalytics.contains(iAnalytics))
            proxyAnalytics.add(iAnalytics)
    }

    //移除代理
    fun unregister(iAnalytics: IAnalytics) {
        if (proxyAnalytics.contains(iAnalytics))
            proxyAnalytics.remove(iAnalytics)
    }

    //初始化
    override fun initConfig(context: Application) {
        proxyAnalytics.forEach { it.initConfig(context) }
    }

    //绑定用户
    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
        proxyAnalytics.forEach { it.bindUserId(uid, userProperty) }
    }

    override fun trackPage(params: ArrayList<String>, lifecycle: Lifecycle, pageClass: Class<*>) {
        proxyAnalytics.forEach { it.trackPage(params, lifecycle, pageClass) }
    }

    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
//        Log.d("lucas","[trackEvent]  eventName:$eventName")
        proxyAnalytics.forEach { it.trackEvent(eventName, param) }
    }

}