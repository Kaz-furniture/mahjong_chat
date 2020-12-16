package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import com.kaz_furniture.mahjongChat.viewModel.ProfileEditViewModel

class ProfileEditActivity: BaseActivity() {

    lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.presentUserId = FirebaseAuth.getInstance().currentUser?.uid ?:launchLoginActivity()
        viewModel.loadUserInfo()
        val binding: ActivityProfileEditBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        binding.lifecycleOwner = this
        binding.name = viewModel.editedName
        binding.introduction = viewModel.editedIntroduction
        viewModel.makeLogout.observe(this, Observer {
            launchLoginActivity()
        })
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = viewModel.canSubmit.value
        })
        binding.saveButton.setOnClickListener {
            viewModel.editUpload(this)
        }
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileEditActivity::class.java)
        }
    }
}