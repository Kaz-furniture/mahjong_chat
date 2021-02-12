package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityFollowDisplayBinding
import com.kaz_furniture.mahjongChat.viewModel.FollowDisplayViewModel

class FollowDisplayActivity: BaseActivity() {
    private val viewModel: FollowDisplayViewModel by viewModels()
    lateinit var binding: ActivityFollowDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_display)
        binding.lifecycleOwner = this
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FollowDisplayActivity::class.java)
        }
    }
}