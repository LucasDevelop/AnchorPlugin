package com.lucas.analytics_plugin.ext

import org.objectweb.asm.Type

val EMPTY_STR = ""
fun String.log() {
    println("$this")
}

fun String.filePathToClassPath() = replace("/", ".").substring(1, length - 1)

fun String.classPathToFilePath() = "L".plus(replace(".", "/"))

fun String.classPathToType() = Type.getType("L".plus(this).plus(";"))


fun List<Any>.toMap(): HashMap<String, Any> {
    val hashMap = HashMap<String, Any>()
    if (size == 0) {
        return hashMap
    }
    var index = 0
    while (index < size) {
        hashMap[get(index++) as String] = get(index++)
    }
    return hashMap
}