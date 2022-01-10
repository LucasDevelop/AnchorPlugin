package com.lucas.analytics_plugin.proxy.impl.trackmethod

import com.lucas.analytics_plugin.proxy.AbsClassNode
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor

class TrackMethodClassNode(val classWriter: ClassWriter, val classReader: ClassReader) :
    AbsClassNode() {

    override fun visitMethod(
        visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var visitMethod = super.visitMethod(visitor,access, name, descriptor, signature, exceptions)
        visitMethod = TrackMethodAdviceAdapter(this, visitMethod, access, name, descriptor)
        return visitMethod
    }
}