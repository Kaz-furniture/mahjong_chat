package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.viewModel.CreateAccountViewModel
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity
import com.kaz_furniture.mahjongChat.databinding.ActivityCreateAccountBinding
import java.util.*

class CreateAccountActivity: BaseActivity() {

    lateinit var binding: ActivityCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCreateAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        binding.lifecycleOwner = this
        binding.email = viewModel.email
        binding.password = viewModel.password
        binding.passwordConfirm = viewModel.passwordValidate
        binding.name = viewModel.nameInput
        viewModel.canSubmit.observe(this, androidx.lifecycle.Observer {
            binding.canSubmit = it
        })
        viewModel.nameError.observe(this, androidx.lifecycle.Observer {
            binding.nameError = it
        })
        viewModel.emailError.observe(this, androidx.lifecycle.Observer {
            binding.emailError = it
        })
        viewModel.passwordError.observe(this, androidx.lifecycle.Observer{
            binding.passwordError = it
        })
        binding.saveButton.setOnClickListener{
            viewModel.createAuthUser(this, this@CreateAccountActivity)
        }
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }


    companion object {
        fun start(activity: Activity) =
                activity.apply {
                    startActivity(Intent(activity, CreateAccountActivity::class.java))
                }
    }
}