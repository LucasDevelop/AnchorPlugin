package com.example.asmsample

import com.lucas.annotation.TrackPage
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(com.lucas.annotation.TrackPage::class)
        println(com.lucas.annotation.TrackPage::class.qualifiedName)
        println(com.lucas.annotation.TrackPage::class.java.canonicalName)
        println(com.lucas.annotation.TrackPage::class.java.toGenericString())
        println(com.lucas.annotation.TrackPage::class.java.typeName)
        println(com.lucas.annotation.TrackPage::class.java.name)
    }
}