package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.mahjongChat.viewModel.LoginViewModel
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityLoginBinding

class LoginActivity: BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.createUserTextView.setOnClickListener {
            CreateAccountActivity.start(this)
        }
        binding.passwordForget.setOnClickListener {
            PasswordForgetActivity.start(this)
        }
        binding.email = viewModel.email
        binding.password = viewModel.password
        binding.login.setOnClickListener {
            viewModel.login(this,this@LoginActivity)
        }
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = it
        })
        viewModel.emailError.observe(this, Observer {
            binding.emailError = it
        })
        viewModel.passwordError.observe(this, Observer {
            binding.passwordError = it
        })

        viewModel.makeLogout.observe(this, Observer {
            launchLoginActivity()
        })
        binding.emailInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
        binding.container.setOnClickListener{
            hideKeyboard(it)
        }
        binding.passwordInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
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