package com.example.asmsample

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(Date().getByZero())
        println(Date().getByFull())
    }

    //获取零点时间
    fun Date.getByZero() {
        val instance = Calendar.getInstance()
        instance.time  = this
        instance.set(Calendar.HOUR_OF_DAY,0)
        instance.set(Calendar.MINUTE,0)
        instance.set(Calendar.SECOND,0)
        instance.set(Calendar.MILLISECOND,0)
    }

    //获取结束时间
    fun Date.getByFull() {
        val instance = Calendar.getInstance()
        instance.time  = this
        instance.add(Calendar.DAY_OF_MONTH,1)
        instance.set(Calendar.HOUR_OF_DAY,0)
        instance.set(Calendar.MINUTE,0)
        instance.set(Calendar.SECOND,0)
        instance.set(Calendar.MILLISECOND,0)
        instance.add(Calendar.MILLISECOND,-1)
    }
}