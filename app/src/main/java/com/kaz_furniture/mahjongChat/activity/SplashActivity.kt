package com.kaz_furniture.mahjongChat.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity

class SplashActivity: BaseActivity() {

    private val splashTime = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseAuth.getInstance().currentUser?.also {
//            FirebaseAuth.getInstance().signOut()
            startMainActivity()
        } ?:run {
            startLoginActivity()
        }
    }


    private fun startMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            FirebaseAuth.getInstance().signOut()
            MainActivity.start(this)
        }, splashTime)
    }

    private fun startLoginActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            LoginActivity.start(this)
        }, splashTime)
    }
}