package com.lucas.analytics.common.impl

import android.app.Application
import android.util.Log
import com.lucas.analytics.BuildConfig
import com.lucas.analytics.common.IAnalytics

class FirebaseAnalytics : IAnalytics {
    lateinit var firebaseAnalytics: FirebaseAnalytics
    var isDebug = BuildConfig.DEBUG

    /**
     * 初始化
     *
     */
    override fun initConfig(context: Application) {
//        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    /**
     * 设置用户信息
     *
     * @param uid 用户ID
     * @param userProperty 用户其他参数
     */
    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
        if (isDebug) {
            var paramStr = ""
            userProperty?.forEach { paramStr = paramStr.plus(it.key).plus("->").plus(it.value).plus(";") }
//            "bindUserId():uid:$uid,userProperty:${paramStr}".log()
        }
        if (checkInit()) return
//        firebaseAnalytics.setUserId(uid)
//        userProperty?.forEach {
//            firebaseAnalytics.setUserProperty(it.key, it.value)
//        }
    }

    override fun trackPage(
        params: Map<String, Any>,
        methodName: String,
        des: String,
        pageClass: Class<*>
    ) {
        if (isDebug) {
//            "trackPage():moduleName:${moduleName},pageName:$pageName,pageClass:$pageClass".log()
        }
//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
//            putString(FirebaseAnalytics.Param.SCREEN_NAME, moduleName.plus("-").plus(pageName).plus("-").plus(childPageName))
//            putString(FirebaseAnalytics.Param.SCREEN_CLASS, pageClass.simpleName)
//        })
    }

    /**
     * 跟踪事件
     *
     * @param eventName 事件名称
     * @param param 需上报的数据
     */
    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
        if (isDebug) {
            var paramStr = ""
            param?.forEach { paramStr = paramStr.plus(it.key).plus("->").plus(it.value).plus(";") }
//            "trackEvent():eventName:$eventName,size:${param?.size},param:${paramStr}".log()
        }
//        firebaseAnalytics.logEvent(eventName, Bundle().apply {
//            param?.forEach {
//                putString(it.key, it.value.toString())
//            }
//        })
    }

    /**
     * 检查参数
     *
     * @return
     */
    private fun checkInit(): Boolean {
        return !this::firebaseAnalytics.isInitialized
    }

    fun String.log() {
        Log.d("firebase", this)
    }
}