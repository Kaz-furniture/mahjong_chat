package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityDmDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.DMDetailViewModel

class DMDetailActivity: BaseActivity() {
    private val viewModel: DMDetailViewModel by viewModels()
    lateinit var binding: ActivityDmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dm_detail)
        binding.lifecycleOwner = this

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DMDetailActivity::class.java)
        }
    }
}