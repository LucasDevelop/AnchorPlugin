package com.lucas.analytics_plugin.proxy.impl.trackpage

import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.EMPTY_STR
import com.lucas.analytics_plugin.ext.classPathToType
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

class TrackPageClassNodeProxyImpl(classWriter: ClassWriter) : ClassNodeProxy(classWriter) {

    var isExistMethod = false//是否已重写了方法
    var isEnable = false//是否存在注解
    var pageName = EMPTY_STR
    var moduleName = EMPTY_STR
    var childPageName = EMPTY_STR

    override fun filterVisitorAnnotation(descriptor: String?, visible: Boolean): Boolean {
        val b = descriptor == AnnotationReflect.trackPage
        if (!isEnable) {
            isEnable = b
        }
        return b
    }

    override fun filterVisitorMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): Boolean {
        if (isExistMethod) return true
        isExistMethod = (name == "onResume" && descriptor == "()V")
//        "TrackPageClassNodeProxyImpl->filterVisitorMethod()->methodName:${name},isEnable:$isEnable,className:$className,isExistMethod:$isExistMethod".log()
        if (isEnable) {
            pageName = commonAnnotationVisitor!!.annoParam["pageName"] as String
            moduleName = commonAnnotationVisitor!!.annoParam["moduleName"] as String
            childPageName = commonAnnotationVisitor!!.annoParam["childPageName"] as String
        }
        return isExistMethod
    }

    override fun proxyVisitorEnd() {
        if (!isEnable) return
        if (isExistMethod) return
//        "TrackPageClassNodeProxyImpl->proxyVisitorEnd()->pageName:${pageName},superName:$superName,className:$className,classWriter:$classWriter".log()
        //新增onResume方法
        val methodVisitor =
            classWriter.visitMethod(Opcodes.ACC_PROTECTED, "onResume", "()V", null, null)
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            superName,
            "onResume",
            "()V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/lucas/analytics/Analytics",
            "INSTANCE",
            "Lcom/lucas/analytics/Analytics;"
        )
        methodVisitor.visitLdcInsn(moduleName)
        methodVisitor.visitLdcInsn(pageName)
        methodVisitor.visitLdcInsn(childPageName)
        methodVisitor.visitLdcInsn(className.classPathToType())
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/lucas/analytics/Analytics",
            "trackPage",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)V",
            false
        )
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label3 = Label()
        methodVisitor.visitLabel(label3)
        methodVisitor.visitLocalVariable(
            "this",
            "L".plus(className).plus(";"),
            null,
            label0,
            label3,
            0
        )
        methodVisitor.visitMaxs(5, 1)
        methodVisitor.visitEnd()
    }
}