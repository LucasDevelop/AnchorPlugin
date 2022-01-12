package com.lucas.analytics.utils

import com.lucas.analytics.common.Lifecycle

object LifecycleUtil {

    fun byLifecycle(methodName: String, des: String) =
        if (methodName == Lifecycle.onCreate.methodName && des == Lifecycle.onCreate.des)
            Lifecycle.onCreate
        else if (methodName == Lifecycle.onStart.methodName && des == Lifecycle.onStart.des)
            Lifecycle.onStart
        else if (methodName == Lifecycle.onPause.methodName && des == Lifecycle.onPause.des)
            Lifecycle.onPause
        else if (methodName == Lifecycle.onResume.methodName && des == Lifecycle.onResume.des)
            Lifecycle.onResume
        else if (methodName == Lifecycle.onStop.methodName && des == Lifecycle.onStop.des)
            Lifecycle.onStop
        else if (methodName == Lifecycle.onDestroy.methodName && des == Lifecycle.onDestroy.des)
            Lifecycle.onDestroy
        else
            Lifecycle.onDestroy
}