package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileBinding
import com.kaz_furniture.mahjongChat.fragment.HomeFragment
import com.kaz_furniture.mahjongChat.view.PostView
import com.kaz_furniture.mahjongChat.viewModel.ProfileViewModel

class ProfileActivity: BaseActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var binding:ActivityProfileBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.lifecycleOwner = this
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userId = intent.getStringExtra(KEY) ?:""
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPostList(userId)
        }
        binding.userId = userId
        viewModel.getUserInfo(userId)
        viewModel.getPostList(userId)
        viewModel.item.observe(this, Observer {
            binding.postView.customAdapter.refresh(it)
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.user.observe(this, Observer {
            binding.explanation.text = it.introduction
            title = it.name
        })
        viewModel.postSelected.observe(this, Observer {
            openDetail(it)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun openDetail(post: Post) {
        val intent = PostDetailActivity.newIntent(this, post)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }


    companion object {
        private const val REQUEST_CODE_DETAIL = 3001
        private const val KEY = "KEY_ID"
        fun newIntent(context: Context, id: String?): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY, id)
            }
        }
    }
}