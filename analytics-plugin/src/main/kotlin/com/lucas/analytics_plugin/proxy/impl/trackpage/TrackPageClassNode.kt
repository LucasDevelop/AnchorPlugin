package com.lucas.analytics_plugin.proxy.impl.trackpage

import com.lucas.analytics_plugin.config.ActivityLifecycle
import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.classPathToType
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.proxy.AbsClassNode
import org.objectweb.asm.*


class TrackPageClassNode(val classWriter: ClassWriter, val classReader: ClassReader) :
    AbsClassNode() {

    val lifecycles = mutableListOf<ActivityLifecycle>()//已重载的方法

    override fun visitMethod(
        visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var visitMethod =
            super.visitMethod(visitor, access, name, descriptor, signature, exceptions)
        //限制仅支持activity\fragment fixme 暂未实现
//        if (className=="com/example/asmsample/MainActivity"){
//            "interfaces:${interfaces.isNullOrEmpty()}".log()
//            interfaces?.forEach {
//                "interfaces:$it".log()
//            }
//        }
        AnnotationReflect.registerLifecycles
            .find { it.methodName == name && descriptor == it.des }?.let { method ->
                "扫描方法：visitMethod:class->${className},method->${name}".log()
                lifecycles.add(method)
                visitMethod = TrackPageAdviceAdapter(
                    this,
                    visitMethod,
                    access,
                    name,
                    descriptor
                )
            }
        return visitMethod
    }


    override fun visitEnd() {
        if (classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
            classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                "annoParam[\"params\"]：${annoParam["params"]}，${annoParam["params"]?.javaClass}".log()
                val params = annoParam["params"] as? ArrayList<*>
                if (params.isNullOrEmpty()) return
                val toMutableList = AnnotationReflect.registerLifecycles.toMutableList()
                toMutableList.removeAll(lifecycles)
                toMutableList.forEach {
                    when (it) {
                        ActivityLifecycle.onCreate -> addOnCreate()
                        ActivityLifecycle.onStart,
                        ActivityLifecycle.onResume,
                        ActivityLifecycle.onPause,
                        ActivityLifecycle.onStop,
                        ActivityLifecycle.onDestroy -> addFullLifecycleMethod(it)
                    }
                    "全量插入【trackPage】<${className}>[methodName:${it.methodName},des:${it.des}]:params:$params".log()
                }
            }
        }
    }

    private fun addFullLifecycleMethod(lifecycle: ActivityLifecycle) {
        val methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_PROTECTED,
            lifecycle.methodName,
            lifecycle.des,
            null,
            null
        )
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            superName,
            lifecycle.methodName,
            lifecycle.des,
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 2)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/util/ArrayList", "<init>", "()V",
            false
        )
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 1)
        var startLabel: Label? = null
        if (classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
            classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                val params = annoParam["params"] as? ArrayList<*>
                params!!.forEach {
                    if (startLabel == null) {
                        startLabel = Label()
                        methodVisitor.visitLabel(startLabel)
                    } else {
                        methodVisitor.visitLabel(Label())
                    }
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
                    methodVisitor.visitLdcInsn(it)
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z",
                        false
                    )
                    methodVisitor.visitInsn(Opcodes.POP)
                }
            }
        }

        val label6 = Label()
        methodVisitor.visitLabel(label6)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/lucas/analytics/Analytics",
            "INSTANCE",
            "Lcom/lucas/analytics/Analytics;"
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/lucas/analytics/common/Lifecycle",
            lifecycle.methodName,
            "Lcom/lucas/analytics/common/Lifecycle;"
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
//        methodVisitor.visitLdcInsn(className.classPathToType())
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Object",
            "getClass",
            "()Ljava/lang/Class;",
            false
        );
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/lucas/analytics/Analytics",
            "trackPage",
            "(Ljava/util/ArrayList;Lcom/lucas/analytics/common/Lifecycle;Ljava/lang/Class;)V",
            false
        )
        val label7 = Label()
        methodVisitor.visitLabel(label7)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label8 = Label()
        methodVisitor.visitLabel(label8)
        methodVisitor.visitLocalVariable(
            "params",
            "Ljava/util/ArrayList;",
            null,
            startLabel,
            label8,
            1
        )
        methodVisitor.visitLocalVariable(
            "this",
            className.classPathToType().descriptor,
            null,
            label0,
            label8,
            0
        )
        methodVisitor.visitMaxs(4, 3)
        methodVisitor.visitEnd()
    }

    private fun addOnCreate() {
        val methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_PROTECTED,
            ActivityLifecycle.onCreate.methodName,
            ActivityLifecycle.onCreate.des,
            null,
            null
        )
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(
            0,
            "Lorg/jetbrains/annotations/Nullable;",
            false
        )
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            superName,
            ActivityLifecycle.onCreate.methodName,
            ActivityLifecycle.onCreate.des,
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 3)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/util/HashMap",
            "<init>",
            "()V",
            false
        )
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
        var startLabel: Label? = null
        if (classAnnotationVisitors.containsKey(AnnotationReflect.trackPage)) {
            classAnnotationVisitors[AnnotationReflect.trackPage]!!.apply {
                val params = annoParam["params"] as? Array<String>
                this.annoParam.forEach {
                    if (startLabel == null) {
                        startLabel = Label()
                        methodVisitor.visitLabel(startLabel)
                    } else {
                        methodVisitor.visitLabel(Label())
                    }
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
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
        val label6 = Label()
        methodVisitor.visitLabel(label6)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/lucas/analytics/Analytics",
            "INSTANCE",
            "Lcom/lucas/analytics/Analytics;"
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map")
        methodVisitor.visitLdcInsn(ActivityLifecycle.onCreate.methodName)
        methodVisitor.visitLdcInsn(ActivityLifecycle.onCreate.des)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Object",
            "getClass",
            "()Ljava/lang/Class;",
            false
        )
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/lucas/analytics/Analytics",
            "trackPage",
            "(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)V",
            false
        )
        val label7 = Label()
        methodVisitor.visitLabel(label7)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label8 = Label()
        methodVisitor.visitLabel(label8)
        methodVisitor.visitLocalVariable(
            "params",
            "Ljava/util/HashMap;",
            null,
            startLabel,
            label8,
            2
        )
        methodVisitor.visitLocalVariable(
            "this",
            className.classPathToType().descriptor,
            null,
            label0,
            label8,
            0
        )
        methodVisitor.visitLocalVariable(
            "savedInstanceState",
            "Landroid/os/Bundle;",
            null,
            label0,
            label8,
            1
        )
        methodVisitor.visitMaxs(5, 4)
        methodVisitor.visitEnd()
    }


}