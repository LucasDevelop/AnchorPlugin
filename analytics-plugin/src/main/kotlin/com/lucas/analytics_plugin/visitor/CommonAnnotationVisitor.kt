package com.lucas.analytics_plugin.visitor

import com.lucas.analytics_plugin.ext.EMPTY_STR
import com.lucas.analytics_plugin.ext.log
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

/**
 * File CommonAnnotationVisitor.kt
 * Date 6/7/21
 * Author lucas
 * Introduction 通用读取注解参数
 */
class CommonAnnotationVisitor(visitAnnotation: AnnotationVisitor) :
    AnnotationVisitor(Opcodes.ASM5, visitAnnotation) {
    val annoParam = HashMap<String, Any>()

    override fun visit(name: String, value: Any?) {
        annoParam[name] = value ?: EMPTY_STR
//        "方法上的注解 visit->name:$name,value:$value".log()
        super.visit(name, value)
    }

    //读取可变参数
    override fun visitArray(name: String): AnnotationVisitor {
//        "方法上的注解 visitArray->name:$name".log()
        val visitArray = super.visitArray(name)
        if (visitArray != null) {
            val commonAnnotationArrayVisitor = CommonAnnotationArrayVisitor(visitArray)
            annoParam[name] = commonAnnotationArrayVisitor.annoParam
            return commonAnnotationArrayVisitor
        }
        return visitArray
    }

    override fun visitEnd() {
        super.visitEnd()
        //打印参数
//        var temp = ""
//        annoParam.forEach {
//            temp = temp.plus(it.key).plus(":").plus(it.value).plus(",")
//        }
//        temp.log()
    }

    inner class CommonAnnotationArrayVisitor(visitArray: AnnotationVisitor) : AnnotationVisitor(Opcodes.ASM5,visitArray) {
        val annoParam = ArrayList<Any>()
        override fun visit(name: String?, value: Any?) {
//            "方法上的注解 inner visit->name:$name,value:$value".log()
            annoParam.add(value ?: EMPTY_STR)
            super.visit(name, value)
        }

        override fun visitArray(name: String?): AnnotationVisitor {
//            "方法上的注解 inner visitArray->name:$name".log()
            return super.visitArray(name)
        }
    }
}