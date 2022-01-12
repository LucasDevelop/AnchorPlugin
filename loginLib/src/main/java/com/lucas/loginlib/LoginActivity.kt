package com.lucas.loginlib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lucas.analytics.annotation.TrackMethod
import com.lucas.analytics.annotation.TrackPage

@TrackPage("登录页", "",  "")
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        onSuccess("hahahahaha")
    }

    @TrackMethod("回调", trackParams = ["username"])
    fun onSuccess(username: String) {

    }
}