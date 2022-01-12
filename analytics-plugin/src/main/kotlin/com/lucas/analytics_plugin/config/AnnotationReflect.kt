package com.lucas.analytics_plugin.config


object AnnotationReflect {
    //注解字节码名称
    val trackPage = "Lcom/lucas/analytics/annotation/TrackPage;"
    val trackMethod = "Lcom/lucas/analytics/annotation/TrackMethod;"

    //插桩生命周期
    val registerLifecycles = listOf(
        ActivityLifecycle.onCreate,
        ActivityLifecycle.onStart,
        ActivityLifecycle.onResume,
        ActivityLifecycle.onPause,
        ActivityLifecycle.onStop,
        ActivityLifecycle.onDestroy,
    )
}