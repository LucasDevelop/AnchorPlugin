package com.lucas.analytics_plugin.proxy.impl.trackmethod

import com.lucas.analytics_plugin.config.AnnotationReflect
import com.lucas.analytics_plugin.ext.log
import com.lucas.analytics_plugin.visitor.CommonAnnotationVisitor
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode

class TrackMethodAdviceAdapter(
    val trackMethodClassNode: TrackMethodClassNode,
    val methodVisitor: MethodVisitor,
    access: Int,
    val methodName: String?,
    val descriptor: String?
) : AdviceAdapter(
    Opcodes.ASM5,
    methodVisitor,
    access, methodName, descriptor
) {
    private var methodAnnotationVisitor: CommonAnnotationVisitor? = null
    private var annoParamKey = ArrayList<String>()//trackParams的key
    private val paramIndexMap = HashMap<String, Int>()//参数索引
    private var paramValIndex = 0

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        var visitAnnotation = super.visitAnnotation(descriptor, visible)
        if (descriptor == AnnotationReflect.trackMethod) {
            methodAnnotationVisitor = CommonAnnotationVisitor(visitAnnotation)
            return methodAnnotationVisitor!!
        }
        return visitAnnotation
    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
        paramValIndex++
        if (name!=null&&annoParamKey.contains(name))
            paramIndexMap[name] = index
    }


    override fun visitEnd() {
        super.visitEnd()
//        if (methodName == "login"){
//            methodAnnotationVisitor?.annoParam?.forEach {
//                "key:${it.key},value:${it.value}".log()
//            }
//        }

        if (methodAnnotationVisitor?.annoParam?.containsKey("des") != true ||
            methodAnnotationVisitor?.annoParam?.get("des") == null
        ) return
        methodAnnotationVisitor?.apply {
//            checkValParams()
//            "paramValIndex:$paramValIndex".log()
//            paramIndexMap.forEach {
//                "key:${it.key},value:${it.value}".log()
//            }
//        "注入事件->methodName:$methodName,paramIndexMap.size:${paramIndexMap.size}".log()

            val label1 = Label()
            methodVisitor.visitLabel(label1)
            methodVisitor.visitTypeInsn(NEW, "java/util/HashMap")
            methodVisitor.visitInsn(DUP)
            methodVisitor.visitMethodInsn(
                INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false
            )
            methodVisitor.visitVarInsn(ASTORE, paramValIndex)
            paramIndexMap.forEach {
                val label2 = Label()
                methodVisitor.visitLabel(label2)
                methodVisitor.visitVarInsn(ALOAD, paramValIndex)
                methodVisitor.visitLdcInsn(it.key)
                methodVisitor.visitVarInsn(ALOAD, it.value)
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/util/HashMap",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
                methodVisitor.visitInsn(POP)
            }
            val label4 = Label()
            methodVisitor.visitLabel(label4)
            methodVisitor.visitFieldInsn(
                GETSTATIC,
                "com/lucas/analytics/Analytics",
                "INSTANCE",
                "Lcom/lucas/analytics/Analytics;"
            )
            methodVisitor.visitLdcInsn(
                methodAnnotationVisitor?.annoParam?.get("des") ?: "TrackMethod未设置des"
            )
            methodVisitor.visitVarInsn(ALOAD, paramValIndex)
            methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/lucas/analytics/Analytics",
                "trackEvent",
                "(Ljava/lang/String;Ljava/util/HashMap;)V",
                false
            )
            val label5 = Label()
            methodVisitor.visitLabel(label5)
            methodVisitor.visitInsn(RETURN)
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
            "增量插入【trackMethod】<${trackMethodClassNode.className}>[methodName:$methodName,des:${descriptor}]".log()
        }
    }

    /**
     * 获取方法的本地变量(包括方法参数)：
     * 由于visitLocalVariable在onEnterMethod之后执行所以暂时无法通过visitor获取参数表（我TM裂开了！）
     * 暂时使用反射+visitor获取参数表
     *
     */
//    private fun checkValParams() {
//        if (methodAnnotationVisitor?.annoParam?.containsKey("trackParams") == true) {
//            annoParamKey = methodAnnotationVisitor!!.annoParam["trackParams"] as ArrayList<String>
//        }
//        //获取参数表
//        val classNode = ClassNode()
//        trackMethodClassNode.classReader.accept(classNode, 0)
//        classNode.methods.find {
////            if (methodName == "register")
////                "checkValParams->it.name:${it.name},methodName:${methodName}".log()
//            it.name == methodName && it.desc == descriptor
//        }?.also {
//            it.localVariables?.forEach {
//                paramValIndex++
//                if (annoParamKey.contains(it.name))
//                    paramIndexMap[it.name] = it.index
////                if (methodName == "register")
////                    "method node localVariables:name:${it.name},desc:${it.desc},index:${it.index},end:${it.end},signature:${it.signature},start:${it.start.type}".log()
//            }
//        }
//    }

}