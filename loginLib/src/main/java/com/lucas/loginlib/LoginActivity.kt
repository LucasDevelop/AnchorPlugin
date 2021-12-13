package com.lucas.loginlib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lucas.annotation.TrackMethod
import com.lucas.annotation.TrackPage

@TrackPage(moduleName = "登录页", pageName = "", childPageName = "")
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