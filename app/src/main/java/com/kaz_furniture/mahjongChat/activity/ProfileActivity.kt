package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileBinding
import com.kaz_furniture.mahjongChat.view.PostView
import com.kaz_furniture.mahjongChat.viewModel.ProfileViewModel

class ProfileActivity: BaseActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var binding:ActivityProfileBinding
    private lateinit var adapter: PostListAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPostList()
        }
        userId = intent.getStringExtra(KEY) ?:""
        binding.userId = userId
        viewModel.getPostList()
        viewModel.item.observe(this, Observer {
            binding.postView.customAdapter.refresh(it)
            binding.swipeRefresh.isRefreshing = false
        })
    }

    companion object {
        private const val KEY = "KEY_ID"
        fun newIntent(context: Context, id: String?): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY, id)
            }
        }
    }
}