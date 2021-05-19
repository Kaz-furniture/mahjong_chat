package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
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
            login()
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

    private fun login() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(viewModel.email.value ?:"", viewModel.password.value ?:"")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finishAffinity()
                    SplashActivity.start(this)
                } else {
                    Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        fun start(activity: Activity) =
            activity.apply {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
    }
}