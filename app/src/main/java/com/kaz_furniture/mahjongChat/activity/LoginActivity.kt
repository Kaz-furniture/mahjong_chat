package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.viewModel.LoginViewModel
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity
import com.kaz_furniture.mahjongChat.databinding.ActivityLoginBinding

class LoginActivity: BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindingData: ActivityLoginBinding? = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )
        binding = bindingData ?:return
        binding.lifecycleOwner = this
        binding.createUserTextView.setOnClickListener {
            CreateAccountActivity.start(this)
        }
        binding.canSubmit = viewModel.canSubmit
        binding.email= viewModel.email
        binding.password = viewModel.password
        binding.login.setOnClickListener {
            viewModel.login(this,this@LoginActivity)
        }
    }

    companion object {
        fun start(activity: Activity) =
            activity.apply {
                finishAffinity()
                startActivity(Intent(activity, LoginActivity::class.java))
            }
    }
}