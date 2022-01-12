package com.lucas.analytics.common

enum class Lifecycle(val methodName: String, val des:String) {
    onCreate("onCreate","(Landroid/os/Bundle;)V"),
    onStart("onStart","()V"),
    onResume("onResume","()V"),
    onPause("onPause","()V"),
    onStop("onStop","()V"),
    onDestroy("onDestroy","()V"),
}