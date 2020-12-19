package com.kaz_furniture.mahjongChat.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivitySplashBinding
import com.kaz_furniture.mahjongChat.viewModel.SplashViewModel

class SplashActivity: BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.lifecycleOwner = this
        setContentView(R.layout.activity_splash)
        viewModel.checkAccount(this)
        viewModel.makeLogout.observe(this, Observer {
            launchLoginActivity()
        })

    }

}