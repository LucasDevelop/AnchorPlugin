package com.example.asmsample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lucas.analytics.annotation.TrackMethod
import com.lucas.analytics.annotation.TrackPage

@TrackPage("首页","APP","app")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        startActivity(Intent(this, JavaActivity::class.java))
//        login("ave", "ddddd")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
//        Analytics.trackPage("moduleName","pageName",this.javaClass)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @TrackMethod(des ="用户登录", trackParams = ["name", "temp"])
    private fun login(name: String, pwd: String) {
        var temp = "temp value"
        temp = temp.plus(" add value")
        val findViewById = findViewById<View>(R.id.v_text)
        findViewById.setOnClickListener {
            Log.d("lucas","view click")
        }
//        Log.d("lucas", "end")
//        val param = HashMap<String,Any>()
//        param.put("name",name)
//        param.put("pwd",pwd)
//        Analytics.trackEvent("用户登录", param)
    }

}