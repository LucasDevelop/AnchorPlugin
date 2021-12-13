package com.lucas.analytics_plugin.proxy.impl.trackmethod

import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class TrackMethodClassNodeProxyImpl(classWriter: ClassWriter, val classReader: ClassReader) :
    ClassNodeProxy(classWriter) {

    //不处理class上的注解
    override fun filterVisitorAnnotation(descriptor: String?, visible: Boolean): Boolean = false

    //扫描所有方法
    override fun filterVisitorMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): Boolean = true

    override fun proxyVisitorEnd() {
    }
}