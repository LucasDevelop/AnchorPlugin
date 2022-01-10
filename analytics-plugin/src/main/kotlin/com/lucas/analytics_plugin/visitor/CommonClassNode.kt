package com.lucas.analytics_plugin.visitor

import com.lucas.analytics_plugin.proxy.AbsClassNode
import com.lucas.analytics_plugin.proxy.impl.trackmethod.TrackMethodClassNode
import com.lucas.analytics_plugin.proxy.impl.trackpage.TrackPageClassNode
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode

/**
 * File TrackClassNode.kt
 * Date 2021/6/11
 * Author lucas
 * Introduction 扫描class的节点
 */
class CommonClassNode(val classWriter: ClassWriter, val classReader: ClassReader) :
    ClassNode(Opcodes.ASM5), Opcodes {
    private val classNodeProxy: Array<AbsClassNode> by lazy {
        arrayOf(
            TrackPageClassNode(classWriter, classReader),
            TrackMethodClassNode(classWriter, classReader),
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
        super.visit(version, access, name, signature, superName, interfaces)
        classNodeProxy.forEach { it.visit(version, access, name, signature, superName, interfaces) }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        var visitAnnotation = super.visitAnnotation(descriptor, visible)
        classNodeProxy.forEach {
            val commonAnnotationVisitor = CommonAnnotationVisitor(visitAnnotation)
            visitAnnotation = commonAnnotationVisitor
            it.classAnnotationVisitors[descriptor!!] = commonAnnotationVisitor
        }
        return visitAnnotation
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        classNodeProxy.forEach {
            visitMethod = it.visitMethod(visitMethod,access, name, descriptor, signature, exceptions)
        }
        return visitMethod
    }

    override fun visitAttribute(attribute: Attribute?) {
        super.visitAttribute(attribute)
        classNodeProxy.forEach { it.visitAttribute(attribute) }
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        var visitField = super.visitField(access, name, descriptor, signature, value)
        classNodeProxy.forEach {
            visitField = it.visitField(visitField,access, name, descriptor, signature, value)
        }
        return visitField
    }

    override fun visitInnerClass(
        name: String?,
        outerName: String?,
        innerName: String?,
        access: Int
    ) {
        super.visitInnerClass(name, outerName, innerName, access)
        classNodeProxy.forEach { it.visitInnerClass(name, outerName, innerName, access) }
    }

    override fun visitModule(name: String?, access: Int, version: String?): ModuleVisitor {
        var visitModule = super.visitModule(name, access, version)
        classNodeProxy.forEach { visitModule = it.visitModule(visitModule,name, access, version) }
        return visitModule
    }

    override fun visitNestHost(nestHost: String?) {
        super.visitNestHost(nestHost)
        classNodeProxy.forEach { it.visitNestHost(nestHost) }
    }

    override fun visitNestMember(nestMember: String?) {
        super.visitNestMember(nestMember)
        classNodeProxy.forEach { it.visitNestMember(nestMember) }
    }

    override fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
        super.visitOuterClass(owner, name, descriptor)
        classNodeProxy.forEach { it.visitOuterClass(owner, name, descriptor) }
    }

    override fun visitSource(file: String?, debug: String?) {
        super.visitSource(file, debug)
        classNodeProxy.forEach { it.visitSource(file, debug) }
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        var visitTypeAnnotation = super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
        classNodeProxy.forEach {
            visitTypeAnnotation = it.visitTypeAnnotation(visitTypeAnnotation,typeRef, typePath, descriptor, visible)
        }
        return visitTypeAnnotation
    }

    override fun visitEnd() {
        super.visitEnd()
        classNodeProxy.forEach {
            it.visitEnd()
        }
    }
}