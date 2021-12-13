package com.lucas.analytics_plugin.proxy.impl.trackmethod

import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.proxy.ClassNodeProxy
import com.lucas.analytics_plugin.proxy.MethodVisitorProxy
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode

class TrackMethodMethodVisitorProxyImpl(
    val className: String,
    val methodName: String,
    val descriptor: String?,
    val methodVisitor: MethodVisitor,
    val classNode: Array<ClassNodeProxy>,
    val classReader: ClassReader
) : MethodVisitorProxy() {
    private var annoParamKey = ArrayList<String>()//trackParams的key
    private val paramIndexMap = HashMap<String, Int>()//参数索引
    private var isEnable = false
    private var paramValIndex = 0

    override fun proxyVisitorCode() {
    }

    override fun isParseAnnotation(descriptor: String?, visible: Boolean): Boolean {
        val b = descriptor == AnnotationReflect.trackMethod
        if (!isEnable) {
            isEnable = b
        }
        return b
    }


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
        if (commonAnnotationVisitor?.annoParam?.containsKey("des") != true ||
            commonAnnotationVisitor?.annoParam?.get("des") == null
        ) return
        if (!isEnable) return
        checkValParams()
//        "注入事件->methodName:$methodName,paramIndexMap.size:${paramIndexMap.size}".log()
//        paramIndexMap.forEach {
//            "key:${it.key},value:${it.value}".log()
//        }
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitTypeInsn(AdviceAdapter.NEW, "java/util/HashMap")
        methodVisitor.visitInsn(AdviceAdapter.DUP)
        methodVisitor.visitMethodInsn(
            AdviceAdapter.INVOKESPECIAL,
            "java/util/HashMap",
            "<init>",
            "()V",
            false
        )
        methodVisitor.visitVarInsn(AdviceAdapter.ASTORE, paramValIndex)
        paramIndexMap.forEach {
            val label2 = Label()
            methodVisitor.visitLabel(label2)
            methodVisitor.visitVarInsn(AdviceAdapter.ALOAD, paramValIndex)
            methodVisitor.visitLdcInsn(it.key)
            methodVisitor.visitVarInsn(AdviceAdapter.ALOAD, it.value)
            methodVisitor.visitMethodInsn(
                AdviceAdapter.INVOKEVIRTUAL,
                "java/util/HashMap",
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false
            )
            methodVisitor.visitInsn(AdviceAdapter.POP)
        }
        val label4 = Label()
        methodVisitor.visitLabel(label4)
        methodVisitor.visitFieldInsn(
            AdviceAdapter.GETSTATIC,
            "com/lucas/analytics/Analytics",
            "INSTANCE",
            "Lcom/lucas/analytics/Analytics;"
        )
        methodVisitor.visitLdcInsn(
            commonAnnotationVisitor?.annoParam?.get("des") ?: "TrackMethod未设置des"
        )
        methodVisitor.visitVarInsn(AdviceAdapter.ALOAD, paramValIndex)
        methodVisitor.visitMethodInsn(
            AdviceAdapter.INVOKEVIRTUAL,
            "com/lucas/analytics/Analytics",
            "trackEvent",
            "(Ljava/lang/String;Ljava/util/HashMap;)V",
            false
        )
        val label5 = Label()
        methodVisitor.visitLabel(label5)
        methodVisitor.visitInsn(AdviceAdapter.RETURN)
        val label6 = Label()
        methodVisitor.visitLabel(label6)
        methodVisitor.visitLocalVariable(
            "param",
            "Ljava/util/HashMap;",
            "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Object;>;",
            label1,
            label6,
            paramValIndex
        )
    }

    /**
     * 获取方法的本地变量(包括方法参数)：
     * 由于visitLocalVariable在onEnterMethod之后执行所以暂时无法通过visitor获取参数表（我TM裂开了！）
     * 暂时使用反射+visitor获取参数表
     *
     */
    private fun checkValParams() {
        if (commonAnnotationVisitor?.annoParam?.containsKey("trackParams") == true) {
            annoParamKey = commonAnnotationVisitor!!.annoParam["trackParams"] as ArrayList<String>
        }
        //获取参数表
        val classNode = ClassNode()
        classReader.accept(classNode, 0)
        classNode.methods.find {
//            if (methodName == "register")
//                "checkValParams->it.name:${it.name},methodName:${methodName}".log()
            it.name == methodName && it.desc == descriptor
        }?.also {
            it.localVariables?.forEach {
                paramValIndex++
                if (annoParamKey.contains(it.name))
                    paramIndexMap[it.name] = it.index
//                if (methodName == "register")
//                    "method node localVariables:name:${it.name},desc:${it.desc},index:${it.index},end:${it.end},signature:${it.signature},start:${it.start.type}".log()
            }
        }
    }

    override fun proxyVisitorEnd() {
    }
}