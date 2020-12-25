package com.kaz_furniture.mahjongChat.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileBinding
import com.kaz_furniture.mahjongChat.viewModel.ProfileViewModel

class ProfileActivity: BaseActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var binding:ActivityProfileBinding
    private lateinit var adapter: PostListAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}