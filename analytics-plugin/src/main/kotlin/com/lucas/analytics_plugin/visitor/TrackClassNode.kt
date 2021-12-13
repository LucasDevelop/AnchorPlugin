package com.lucas.analytics_plugin.visitor

import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import com.lucas.analytics_plugin.proxy.impl.trackmethod.TrackMethodClassNodeProxyImpl
import com.lucas.analytics_plugin.proxy.impl.trackpage.TrackPageClassNodeProxyImpl
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode

/**
 * File TrackClassNode.kt
 * Date 2021/6/11
 * Author lucas
 * Introduction 扫描class的节点
 */
class TrackClassNode(val classWriter: ClassWriter, val classReader: ClassReader) :
    ClassNode(Opcodes.ASM5), Opcodes {
    lateinit var className: String

    private val classNodeProxy: Array<ClassNodeProxy> by lazy {
        arrayOf(
            TrackPageClassNodeProxyImpl(classWriter),
            TrackMethodClassNodeProxyImpl(classWriter, classReader),
        )
    }

    init {
        cv = classWriter
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name
        classNodeProxy.forEach {
            it.className = name
            it.superName = superName
        }
//        "TrackPageClassNode->visit()->name:$name,superName:$superName".log()
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        var visitAnnotation = super.visitAnnotation(descriptor, visible)
//        "TrackPageClassNode->visitAnnotation()->descriptor:$descriptor".log()
        var isFilter = false
        classNodeProxy.forEach {
            isFilter = isFilter || it.filterVisitorAnnotation(descriptor, visible)
        }
        if (visitAnnotation != null && isFilter) {
            visitAnnotation = CommonAnnotationVisitor(visitAnnotation)
            classNodeProxy.forEach { it.commonAnnotationVisitor = visitAnnotation }
        }
        return visitAnnotation
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
//        "TrackPageClassNode->visitMethod()->name:$name,descriptor:$descriptor".log()
        var isFilter = false
        classNodeProxy.forEach {
            isFilter =
                isFilter || it.filterVisitorMethod(access, name, descriptor, signature, exceptions)
        }
        if (isFilter) {
            return TrackMethodVisitor(
                classReader,
                classNodeProxy,
                className,
                visitMethod,
                access,
                name,
                descriptor
            )
        }
        return visitMethod
    }


    override fun visitEnd() {
        super.visitEnd()
//        "TrackPageClassNode->visitEnd()->isEnable:$isEnable,isExistMethod:$isExistMethod,pageName:$pageName".log()
        classNodeProxy.forEach { it.proxyVisitorEnd() }

    }
}