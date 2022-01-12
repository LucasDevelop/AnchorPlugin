package com.lucas.analytics.common

import android.app.Application

interface IAnalytics {
    /**
     * 初始化
     *
     */
    fun initConfig(context: Application)

    /**
     * 设置用户信息
     *
     * @param uid 用户ID
     * @param userProperty 用户其他参数
     */
    fun bindUserId(uid: String, userProperty: Map<String, String>? = null)

    /**
     * 跟踪界面,该方法将会在activity活着fragment的onResume中调用
     *
     * @param pageName 界面中文名称
     * @param pageClass 界面class
     */
    fun trackPage(params: ArrayList<String>, lifecycle: Lifecycle, pageClass: Class<*>)

    /**
     * 跟踪事件
     *
     * @param eventName 事件名称
     * @param param 需上报的数据
     */
    fun trackEvent(eventName: String, param: HashMap<String, Any>? = null)
}