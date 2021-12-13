package com.lucas.analytics

import android.app.Application
import com.lucas.analytics.common.IAnalytics
import com.lucas.analytics.common.impl.FirebaseAnalytics

/**
 * File Analytics.kt
 * Date 2021/6/3
 * Author lucas
 * Introduction 工具类
 */
object Analytics : IAnalytics {

    //代理对象
    private val proxyAnalytics = mutableListOf<IAnalytics>(FirebaseAnalytics())

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

    override fun trackPage(
        moduleName: String,
        pageName: String,
        childPageName: String,
        pageClass: Class<*>
    ) {
        proxyAnalytics.forEach { it.trackPage(moduleName, pageName, childPageName, pageClass) }
    }


    /**
     * 跟踪界面,并且过滤部分上报任务
     *
     * @param moduleName 模块名称
     * @param pageName 界面名称
     * @param childPageName 子界面名称
     * @param pageClass 界面源文件名称
     * @param filterAnalytic 需过滤的任务
     */
    fun trackPageFilter(
        moduleName: String,
        pageName: String,
        childPageName: String,
        pageClass: Class<*>,
        filterAnalytic: Array<in IAnalytics>
    ) {
        proxyAnalytics.forEach { analytic ->
            if (filterAnalytic.find { analytic.javaClass == it?.javaClass } == null) {
                analytic.trackPage(moduleName, pageName, childPageName, pageClass)
            }
        }
    }

    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
        proxyAnalytics.forEach { it.trackEvent(eventName, param) }
    }

}