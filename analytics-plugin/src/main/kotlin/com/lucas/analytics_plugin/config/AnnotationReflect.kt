package com.lucas.analytics_plugin.config


object AnnotationReflect {
    //注解字节码名称
    val trackPage = "Lcom/lucas/analytics/annotation/TrackPage;"
    val trackMethod = "Lcom/lucas/analytics/annotation/TrackMethod;"

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