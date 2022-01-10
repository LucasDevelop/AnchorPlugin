package com.lucas.analytics_plugin.proxy.impl.trackpage

import com.lucas.analytics_plugin.config.ActivityLifecycle
import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.classPathToType
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.visitor.CommonAnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode

class TrackPageAdviceAdapter(
    val trackPageClassNode: TrackPageClassNode,
    val methodVisitor: MethodVisitor,
    access: Int,
    val methodName: String?,
    val descriptor: String?
) : AdviceAdapter(
    Opcodes.ASM5,
    methodVisitor,
    access, methodName, descriptor
) {

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (trackPageClassNode.classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
            trackPageClassNode.classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                val isCreate = methodName == ActivityLifecycle.ON_CREATE.methodName
                methodVisitor.visitLabel(Label())
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, if (isCreate) 3 else 2)
                methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
                methodVisitor.visitInsn(Opcodes.DUP)
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "java/util/HashMap",
                    "<init>",
                    "()V",
                    false
                )
                methodVisitor.visitLabel(Label())
                methodVisitor.visitVarInsn(Opcodes.ASTORE, if (isCreate) 2 else 1)
                if (trackPageClassNode.classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
                    trackPageClassNode.classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                        this.annoParam.forEach {
                            methodVisitor.visitLabel(Label())
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, if (isCreate) 2 else 1)
                            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map")
                            methodVisitor.visitLdcInsn(it.key)
                            methodVisitor.visitLdcInsn(it.value)
                            methodVisitor.visitMethodInsn(
                                Opcodes.INVOKEINTERFACE,
                                "java/util/Map",
                                "put",
                                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                                true
                            )
                            methodVisitor.visitInsn(Opcodes.POP)
                        }
                    }
                }
                methodVisitor.visitLabel(Label())
                methodVisitor.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/lucas/analytics/Analytics",
                    "INSTANCE",
                    "Lcom/lucas/analytics/Analytics;"
                )
                methodVisitor.visitVarInsn(Opcodes.ALOAD, if (isCreate) 2 else 1)
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map")
                methodVisitor.visitLdcInsn(methodName)
                methodVisitor.visitLdcInsn(descriptor)
                methodVisitor.visitLdcInsn(trackPageClassNode.className.classPathToType())
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/lucas/analytics/Analytics",
                    "trackPage",
                    "(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)V",
                    false
                )
                "增量插入【trackPage】<${trackPageClassNode.className}>[methodName:$methodName,des:${descriptor}]".log()
            }
        }
    }

    override fun visitCode() {
        super.visitCode()


    }


}