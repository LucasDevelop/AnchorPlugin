package com.lucas.analytics_plugin.proxy

import com.lucas.analytics_plugin.visitor.CommonAnnotationVisitor
import org.objectweb.asm.*
import org.objectweb.asm.tree.ClassNode

abstract class AbsClassNode : Opcodes {
    val classAnnotationVisitors = hashMapOf<String, CommonAnnotationVisitor>()
    lateinit var className: String
    lateinit var superName: String
    var interfaces: Array<out String>? = null

    open fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name
        this.superName = superName ?: ""
        this.interfaces = interfaces
    }

    open fun visitAnnotation(
        annotationVisitor: AnnotationVisitor,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return annotationVisitor
    }

    open fun visitAttribute(attribute: Attribute?) {
    }

    open fun visitField(
        fieldVisitor: FieldVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return fieldVisitor
    }

    open fun visitInnerClass(
        name: String?,
        outerName: String?,
        innerName: String?,
        access: Int
    ) {
    }

    open fun visitModule(
        moduleVisitor: ModuleVisitor,
        name: String?,
        access: Int,
        version: String?
    ): ModuleVisitor {
        return moduleVisitor
    }

    open fun visitNestHost(nestHost: String?) {
    }

    open fun visitNestMember(nestMember: String?) {
    }

    open fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
    }

    open fun visitSource(file: String?, debug: String?) {
    }

    open fun visitTypeAnnotation(
        annotationVisitor: AnnotationVisitor,
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        return annotationVisitor
    }

    open fun visitMethod(
        visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return visitor
    }


    open fun visitEnd() {
    }


}