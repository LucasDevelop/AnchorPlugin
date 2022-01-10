package com.example.asmsample

import android.app.Application
import android.util.Log
import com.lucas.analytics.common.IAnalytics

class EventReportProxy: IAnalytics{
    override fun initConfig(context: Application) {
    }

    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
    }


    override fun trackPage(
        params: Map<String, Any>,
        methodName: String,
        des: String,
        pageClass: Class<*>
    ) {
    }

    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
    }
}