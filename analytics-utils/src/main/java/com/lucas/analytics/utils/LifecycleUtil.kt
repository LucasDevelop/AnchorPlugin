package com.lucas.analytics.utils

import com.lucas.analytics.common.Lifecycle

object LifecycleUtil {

    fun byLifecycle(methodName: String, des: String) =
        if (methodName == Lifecycle.ON_CREATE.methodName && des == Lifecycle.ON_CREATE.des)
            Lifecycle.ON_CREATE
        else if (methodName == Lifecycle.ON_START.methodName && des == Lifecycle.ON_START.des)
            Lifecycle.ON_START
        else if (methodName == Lifecycle.ON_PAUSE.methodName && des == Lifecycle.ON_PAUSE.des)
            Lifecycle.ON_PAUSE
        else if (methodName == Lifecycle.ON_RESUME.methodName && des == Lifecycle.ON_RESUME.des)
            Lifecycle.ON_RESUME
        else if (methodName == Lifecycle.ON_STOP.methodName && des == Lifecycle.ON_STOP.des)
            Lifecycle.ON_STOP
        else if (methodName == Lifecycle.ON_DESTROY.methodName && des == Lifecycle.ON_DESTROY.des)
            Lifecycle.ON_DESTROY
        else
            Lifecycle.ON_DESTROY
}