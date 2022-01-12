# ASM埋点上报插件

- [插件功能](#插件功能)
- [插件源码地址](#插件源码地址)
- [实现原理简述](#实现原理简述)
- [集成步骤](#集成步骤)
- [参考文章](#参考文章)

----

## 插件功能

> 通过注解的方式对界面的埋点以及事件的埋点进行上报，以减少三方埋点对项目代码的入侵性。当前以Firebase的埋点上报为例，插件包含两个模块analytics和analytics-plugin。

> analytics：主要包含自定义注解，firebase的帮助类以及gms/firebase三方库。

> analytics-plugin:ASM的字节码插桩功能实现部分。

----


## 插件源码地址

https://github.com/LucasDevelop/AnchorPlugin

----

## 实现原理简述

> 基于ASM字节码插桩技术，在编译期扫描项目class文件，读取class文件上的注解，当匹配上对应的注解后插入Firebase上报事件的字节码，并重新打包class文件。
----
## 集成步骤

- Step1

> 在项目下的Gradle中加入插件依赖

```groovy
buildscript{
    repositories{
       //添加jitpack仓库
        maven { url "https://jitpack.io" }
    }

    dependencies{
        //插件地址
        classpath "com.github.LucasDevelop.AnchorPlugin:analytics-plugin:1.0.2-release"
       
    }
}

allprojects {
    repositories {
        //添加jitpack仓库
        maven { url "https://jitpack.io" }
    }
}
```

- Setp2

> 在项目的主模块下的Grable中应用插件

```groovy
plugins {
    //应用插件-只需在主模块添加即可
    id 'analytics-plugin'
}

dependencies{
    //依赖插件帮助模块（包含注解和工具类）
    implementation "com.github.LucasDevelop.AnchorPlugin:analytics-utils:1.0.2-release"
}

```

- Setp3

> 自定义的监听回调SampleAnalytics.kt

SampleAnalytics.kt只需实现IAnalytics接口即可

```kotlin
class SampleAnalytics : IAnalytics {
    /**
     * 初始化
     *
     */
    override fun initConfig(context: Application) {
        Log.d("lucas", "初始化")
    }
    /**
     * 设置用户信息
     *
     * @param uid 用户ID
     * @param userProperty 用户其他参数
     */
    override fun bindUserId(uid: String, userProperty: Map<String, String>?) {
        Log.d("lucas", "绑定用户信息")
        Log.d("lucas", "uid:$uid")
        userProperty?.forEach {
            Log.d("lucas", "${it.key}:${it.value}")
        }
    }
    /**
     * 跟踪界面,该方法将会在activity活着fragment的onResume中调用
     *
     * @param params {@link com.lucas.analytics.annotation.TrackPage}中的参数
     * @param lifecycle 生命周期
     * @param pageClass 界面class
     */
    override fun trackPage(params: ArrayList<String>, lifecycle: Lifecycle, pageClass: Class<*>) {
        Log.d("lucas", "【界面】params$params,lifecycle:$lifecycle,pageClass:${pageClass.simpleName}")
    }
    /**
     * 跟踪事件
     *
     * @param eventName 事件名称
     * @param param 需上报的数据
     */
    override fun trackEvent(eventName: String, param: HashMap<String, Any>?) {
        Log.d("lucas", "【事件】eventName:$eventName,param:$param")
    }
}
```

在Application的onCreate中初始化,并注册自定义回调
```kotlin
 //注册监听
        Analytics.register(SampleAnalytics())
        //插件初始化
        Analytics.initConfig(this)
```

> 在用户登录后绑定用户信息

```kotlin
val userProperty = mapOf("username" to "lucas","age" to 18)
Analytics.bindUserId("uuid123456", userProperty)
```

> 在Activity/Fragemnt中添加页面埋点

```kotlin
//TrackPage注解只能用在Activity和Fragment的子类的文件名上，
//在界面被打开时会自动回调IAnalytics中的trackPage方法
@TrackPage("模块名称", "一级界面名称", "二级界面名称")
class PermissionActivity:AppCompatActivity(){

    //被TrackMethod注解标记的方法会在该方法被调用的时候自动上报事件并且上报trackParams中指定的参数以及参数的值(参数包括方法参数以及方法内的本地变量)，可以作用在任何类的任何方法上。
    @TrackMethod(des = "changePage", trackParams=["type","tempParams"])
    private fun changePage(type: String) {
        val tempParams = "temp value"
    }
}
```
----
## 参考资料

- 参考博客

<https://juejin.cn/post/6844904013998243847>

<https://blog.csdn.net/u010976213/article/details/105395590>

<https://juejin.cn/post/6877925000045658119>

- 参考项目

<https://github.com/MichaelJokAr/MultiLanguages>

