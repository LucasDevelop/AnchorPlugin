package com.example.asmsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lucas.analytics.Analytics
import com.lucas.analytics.annotation.TrackMethod
import com.lucas.analytics.annotation.TrackPage
import com.lucas.analytics.common.Lifecycle

@TrackPage("首页", "APP", "app")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, JavaActivity::class.java))
        login("ave", "ddddd")
        supportFragmentManager.beginTransaction().add(R.id.v_fra,MainFragment()).commitNowAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
//        val params = ArrayList<String>()
//        params.add("name1")
//        params.add("name2")
//        params.add("name3")
//        Analytics.trackPage(params,Lifecycle.onStart,this::class.java)
    }

    @TrackMethod(des = "用户登录", trackParams = ["name", "temp"])
    private fun login(name: String, pwd: String) {
        var temp = "temp value"
        temp = temp.plus(" add value")
        val findViewById = findViewById<View>(R.id.v_text)
        findViewById.setOnClickListener {
            Log.d("lucas", "view click")
        }
        Log.d("lucas", "end")
    }

}