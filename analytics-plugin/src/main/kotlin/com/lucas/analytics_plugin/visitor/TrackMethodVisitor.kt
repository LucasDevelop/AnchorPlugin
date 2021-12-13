package com.lucas.analytics_plugin.visitor

import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import com.lucas.analytics_plugin.proxy.MethodVisitorProxy
import com.lucas.analytics_plugin.proxy.impl.trackmethod.TrackMethodMethodVisitorProxyImpl
import com.lucas.analytics_plugin.proxy.impl.trackpage.TrackPageMethodVisitorProxyImpl
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * File TrackMethodVisitor.kt
 * Date 2021/6/11
 * Author lucas
 * Introduction 扫描方法
 */
class TrackMethodVisitor(
    val classReader: ClassReader,
    val classNodeProxy: Array<ClassNodeProxy>,
    val className: String,
    val methodVisitor: MethodVisitor,
    access: Int,
    val methodName: String,
    val descriptor: String?
) :
    AdviceAdapter(
        Opcodes.ASM5,
        methodVisitor,
        access, methodName, descriptor
    ) {

    private val methodVisitorProxy: Array<MethodVisitorProxy> by lazy {
        arrayOf(
            TrackPageMethodVisitorProxyImpl(className,methodName,descriptor, methodVisitor, classNodeProxy),
            TrackMethodMethodVisitorProxyImpl(className,methodName,descriptor, methodVisitor, classNodeProxy,classReader),
        )
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        var visitAnnotation = super.visitAnnotation(descriptor, visible)
        var isFilter = false
        methodVisitorProxy.forEach {
            isFilter = isFilter || it.isParseAnnotation(descriptor, visible)
        }
        if (visitAnnotation != null && isFilter) {
            visitAnnotation = CommonAnnotationVisitor(visitAnnotation)
            methodVisitorProxy.forEach { it.commonAnnotationVisitor = visitAnnotation }
        }
        return visitAnnotation
    }


    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        methodVisitorProxy.forEach { it.onMethodEnter() }
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        methodVisitorProxy.forEach { it.onMethodExit() }
    }

    override fun visitCode() {
        super.visitCode()
        methodVisitorProxy.forEach { it.proxyVisitorCode() }
    }

    override fun visitEnd() {
        super.visitEnd()
        methodVisitorProxy.forEach { it.proxyVisitorEnd() }
    }
}