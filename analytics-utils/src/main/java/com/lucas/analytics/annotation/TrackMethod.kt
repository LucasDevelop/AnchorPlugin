package com.lucas.analytics.annotation

/**
 * File TrackMethod.kt
 * Date 2021/6/3
 * Author lucas
 * Introduction 跟踪函数,支持在任何方法上使用
 * @param des 备注
 * @param trackParams 需要上报的当前方法的参数名称或者方法内的本地变量名称
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TrackMethod(val des: String, vararg val trackParams: String)
