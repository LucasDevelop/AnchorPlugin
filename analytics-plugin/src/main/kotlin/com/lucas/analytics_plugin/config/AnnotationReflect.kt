package com.lucas.analytics_plugin.config

import com.lucas.annotation.ActivityLifecycle
import com.lucas.annotation.TrackMethod
import com.lucas.annotation.TrackPage

object AnnotationReflect {
    //注解字节码名称
    val trackPage = "L${TrackPage::class.java.canonicalName.replace(".","/")};"
    val trackMethod = "L${TrackMethod::class.java.canonicalName.replace(".","/")};"

    //插桩生命周期
    val registerLifecycles = mutableListOf(
        ActivityLifecycle.ON_CREATE,
//        ActivityLifecycle.ON_START,
        ActivityLifecycle.ON_RESUME,
//        ActivityLifecycle.ON_PAUSE,
//        ActivityLifecycle.ON_STOP,
//        ActivityLifecycle.ON_DESTROY,
    )
}