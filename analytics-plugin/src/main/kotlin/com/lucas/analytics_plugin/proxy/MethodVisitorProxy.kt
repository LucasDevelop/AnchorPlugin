package com.lucas.analytics_plugin.proxy

import com.lucas.analytics_plugin.visitor.CommonAnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.TypePath

abstract class MethodVisitorProxy {
    var commonAnnotationVisitor: CommonAnnotationVisitor? = null

    abstract fun proxyVisitorCode()

    abstract fun isParseAnnotation(descriptor: String?, visible: Boolean):Boolean

    abstract fun proxyVisitLocalVariable(name: String?,
                                         descriptor: String?,
                                         signature: String?,
                                         start: Label?,
                                         end: Label?,
                                         index: Int)
    abstract fun onMethodEnter()
    abstract fun onMethodExit()

    abstract fun proxyVisitorEnd()
}