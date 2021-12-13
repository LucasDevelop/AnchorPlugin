package com.lucas.annotation

/**
 * File TrackPageName.kt
 * Date 2021/6/3
 * Author lucas
 * Introduction 跟踪页面注解，使用该注解在class上可记录界面到firebase
 *              目前仅支持Activity、fragment
 * @param moduleName 界面所属模块名称
 * @param pageName 界面名称
 * @param childPageName 二级界面名称
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class TrackPage(val moduleName: String,val pageName: String,val childPageName:String)