package com.kaz_furniture.mahjongChat.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.User
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity

class SplashActivity: BaseActivity() {

    private val splashTime = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseAuth.getInstance().currentUser?.also {
            FirebaseAuth.getInstance().signOut()
            startLoginActivity()
        } ?:run {
            startLoginActivity()
        }
    }


    private fun startMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            FirebaseAuth.getInstance().signOut()
            finishAffinity()
            MainActivity.start(this)
        }, splashTime)
    }

    private fun startLoginActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            finishAffinity()
            LoginActivity.start(this)
        }, splashTime)
    }
}