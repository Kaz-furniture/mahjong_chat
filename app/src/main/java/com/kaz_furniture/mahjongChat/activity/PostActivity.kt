package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import com.kaz_furniture.mahjongChat.viewModel.PostViewModel

class PostActivity: BaseActivity() {
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_post)
        binding.lifecycleOwner = this
        binding.explanation = viewModel.explanationInput
        binding.postButton.setOnClickListener {
            viewModel.post(this)
        }
        viewModel.usersName = intent.getStringExtra("KEY_NAME")
//        viewModel.userId = intent.getStringExtra("KEY_ID")
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
    }
}