package com.lucas.analytics_plugin.proxy

import com.lucas.analytics_plugin.visitor.CommonAnnotationVisitor
import org.objectweb.asm.ClassWriter

abstract class ClassNodeProxy(val classWriter: ClassWriter) {
    var className: String = ""
    var superName: String? = null
    var commonAnnotationVisitor: CommonAnnotationVisitor? = null

    //class visitor annotation 过滤条件
    abstract fun filterVisitorAnnotation(descriptor: String?, visible: Boolean): Boolean

    //visitor method 过滤条件
    abstract fun filterVisitorMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): Boolean

    //visitor end 代理方法
    abstract fun proxyVisitorEnd()
}