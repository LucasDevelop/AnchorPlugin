package com.lucas.analytics_plugin.config

enum class FragmentLifecycle(val methodName: String, val des:String) {
    ON_CREATE("onCreate","(Landroid/os/Bundle;)V"),
    ON_CREATE_VIEW("onCreateView","(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;"),
    ON_START("onStart","()V"),
    ON_RESUME("onResume","()V"),
    ON_PAUSE("onPause","()V"),
    ON_STOP("onStop","()V"),
    ON_DESTROY("onDestroy","()V"),
}