package com.kaz_furniture.mahjongChat.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivitySplashBinding
import com.kaz_furniture.mahjongChat.viewModel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity: BaseActivity() {

    private val splashTime = 5000L
    lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.lifecycleOwner = this
        timeCount()
        setContentView(R.layout.activity_splash)
        viewModel.checkAccount()
        viewModel.makeLogout.observe(this, Observer {
            Handler(Looper.getMainLooper()).postDelayed({
                launchLoginActivity()
            }, splashTime)
        })
        viewModel.tokenGetOK.observe(this, Observer {
            viewModel.checkUserAndTokenGet()
        })
        viewModel.myUserOK.observe(this, Observer {
            viewModel.checkUserAndTokenGet()
        })
        viewModel.userAndTokenOK.observe(this, Observer {
            viewModel.fCMTokenCheck()
        })
        viewModel.postsOK.observe(this, Observer {
            viewModel.checkAllFinished()
        })
        viewModel.usersOK.observe(this, Observer {
            viewModel.checkAllFinished()
        })
        viewModel.tokenCheckOK.observe(this, Observer {
            viewModel.checkAllFinished()
        })
        viewModel.timeCount.observe(this, Observer {
            viewModel.checkAllFinished()
        })
        viewModel.allOK.observe(this, Observer {
            MainActivity.start(this)
        })
    }

    private fun timeCount() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.timeCount.postValue(true)
        }, splashTime)
    }

}