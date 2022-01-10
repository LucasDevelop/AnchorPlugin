package com.lucas.analytics_plugin.config

enum class ActivityLifecycle(val methodName: String,val des:String) {
    ON_CREATE("onCreate","(Landroid/os/Bundle;)V"),
    ON_START("onStart","()V"),
    ON_RESUME("onResume","()V"),
    ON_PAUSE("onPause","()V"),
    ON_STOP("onStop","()V"),
    ON_DESTROY("onDestroy","()V"),
}