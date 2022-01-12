package com.lucas.analytics_plugin.config

enum class ActivityLifecycle(val methodName: String,val des:String) {
    onCreate("onCreate","(Landroid/os/Bundle;)V"),
    onStart("onStart","()V"),
    onResume("onResume","()V"),
    onPause("onPause","()V"),
    onStop("onStop","()V"),
    onDestroy("onDestroy","()V"),
}