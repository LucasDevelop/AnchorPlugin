package com.lucas.analytics_plugin.proxy.impl.trackpage

import com.lucas.analytics_plugin.config.ActivityLifecycle
import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.classPathToType
import com.lucas.analytics_plugin.ext.log
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

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
                val isCreate = methodName == ActivityLifecycle.onCreate.methodName
                methodVisitor.visitLabel(Label())
                methodVisitor.visitInsn(ICONST_0)
                methodVisitor.visitVarInsn(ISTORE, if (isCreate) 3 else 2)
                methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
                methodVisitor.visitInsn(Opcodes.DUP)
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "java/util/ArrayList",
                    "<init>",
                    "()V",
                    false
                )
                methodVisitor.visitLabel(Label())
                methodVisitor.visitVarInsn(Opcodes.ASTORE, if (isCreate) 2 else 1)
                if (trackPageClassNode.classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
                    trackPageClassNode.classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                        val params = annoParam["params"] as? ArrayList<*>
                        params!!.forEach {
                            methodVisitor.visitLabel(Label())
                            methodVisitor.visitVarInsn(Opcodes.ALOAD, if (isCreate) 2 else 1)
                            methodVisitor.visitLdcInsn(it)
                            methodVisitor.visitMethodInsn(
                                Opcodes.INVOKEVIRTUAL,
                                "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z",false
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
//                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map")
//                methodVisitor.visitLdcInsn(methodName)
//                methodVisitor.visitLdcInsn(descriptor)
                methodVisitor.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/lucas/analytics/common/Lifecycle",
                    methodName,
                    "Lcom/lucas/analytics/common/Lifecycle;"
                )
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/lucas/analytics/Analytics",
                    "trackPage",
                    "(Ljava/util/ArrayList;Lcom/lucas/analytics/common/Lifecycle;Ljava/lang/Class;)V",
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