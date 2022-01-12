package com.example.asmsample

import android.app.Application
import android.util.Log
import com.lucas.analytics.common.IAnalytics
import com.lucas.analytics.common.Lifecycle

/**
 * File SampleAnalytics.kt
 * Date 2022/1/12
 * Author lucas
 * Introduction 埋点回调案例
 */
class SampleAnalytics : IAnalytics {
    /**
     * 初始化
     *
     */
    override fun initConfig(context: Application) {
        Log.d("lucas", "初始化")
    }
    /**
     * 设置用户信息
     *
     * @param uid 用户ID
     * @param userProperty 用户其他参数
     */
    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
        Log.d("lucas", "绑定用户信息")
        Log.d("lucas", "uid:$uid")
        userProperty?.forEach {
            Log.d("lucas", "${it.key}:${it.value}")
        }
    }
    /**
     * 跟踪界面,该方法将会在activity活着fragment的onResume中调用
     *
     * @param params {@link com.lucas.analytics.annotation.TrackPage}中的参数
     * @param lifecycle 生命周期
     * @param pageClass 界面class
     */

    override fun trackPage(params: ArrayList<String>, lifecycle: Lifecycle, pageClass: Class<*>) {
        Log.d("lucas", "【界面】params$params,lifecycle:$lifecycle,pageClass:${pageClass.simpleName}")
    }
    /**
     * 跟踪事件
     *
     * @param eventName 事件名称
     * @param param 需上报的数据
     */
    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
        Log.d("lucas", "【事件】eventName:$eventName,param:$param")
    }
}