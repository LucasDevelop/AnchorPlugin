package com.lucas.analytics_plugin.proxy.impl.trackpage

import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.EMPTY_STR
import com.lucas.analytics_plugin.ext.classPathToType
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import com.lucas.analytics_plugin.proxy.MethodVisitorProxy
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

class TrackPageMethodVisitorProxyImpl(
    val className: String,
    val methodName: String,
    val descriptor: String?,
    val methodVisitor: MethodVisitor,
    val classNode: Array<ClassNodeProxy>
) : MethodVisitorProxy() {

    //是否需要插桩
    private fun isNeedInject() =
        AnnotationReflect.registerLifecycles.find { it.methodName == methodName && descriptor == it.des } != null


    override fun proxyVisitorCode() {
        if (!isNeedInject()) return

        val trackPageClassNodeProxyImpl =
            classNode.find { it is TrackPageClassNodeProxyImpl } as? TrackPageClassNodeProxyImpl
        val pageName = trackPageClassNodeProxyImpl?.pageName ?: EMPTY_STR
        val moduleName = trackPageClassNodeProxyImpl?.moduleName ?: EMPTY_STR
        val childPageName = trackPageClassNodeProxyImpl?.childPageName ?: EMPTY_STR
        trackPageClassNodeProxyImpl?.also {
            if (it.isEnable && it.isExistMethod) {
//                "TrackPageMethodVisitorProxyImpl->proxyVisitorCode()->trackPageClassNodeProxyImpl:$trackPageClassNodeProxyImpl,pageName:$pageName,isEnable:${trackPageClassNodeProxyImpl?.isEnable},isExistMethod:${trackPageClassNodeProxyImpl?.isExistMethod}".log()
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
            }
        }
    }

    override fun isParseAnnotation(descriptor: String?, visible: Boolean): Boolean = false


    override fun proxyVisitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
    }

    override fun onMethodEnter() {
    }

    override fun onMethodExit() {
    }


    override fun proxyVisitorEnd() {
    }
}