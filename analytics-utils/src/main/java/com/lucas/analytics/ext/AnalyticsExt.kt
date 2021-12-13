package com.lucas.analytics.ext

//判断对象是否是基本数据类型
fun Any.isBasicType() =
    this is Int || this is String || this is Byte || this is Double || this is Float
            || this is Char