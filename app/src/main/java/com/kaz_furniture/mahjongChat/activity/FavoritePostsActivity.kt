package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityFavoritePostsBinding
import com.kaz_furniture.mahjongChat.fragment.HomeFragment
import com.kaz_furniture.mahjongChat.viewModel.FavoritePostsViewModel

class FavoritePostsActivity: BaseActivity() {
    lateinit var binding: ActivityFavoritePostsBinding
    private val viewModel: FavoritePostsViewModel by viewModels()
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_posts)
        binding.lifecycleOwner = this
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.updatePosts()
            binding.swipeRefresh.isRefreshing = false
        }
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.favoritesList.postValue(allPostList.filter { it.favoriteUserIds.contains(MahjongChatApplication.myUser.userId) })
        viewModel.favoritesList.observe(this, Observer {
            binding.postView.customAdapter.refresh(it)
        })
        viewModel.postSelected.observe(this, Observer {
            launchPostDetailActivity(it)
        })
        title = getString(R.string.favorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL) {
            binding.swipeRefresh.isRefreshing = true
            viewModel.updatePosts()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun launchPostDetailActivity(post: Post) {
        val intent = PostDetailActivity.newIntent(this, post)
        startActivityForResult(intent, REQUEST_CODE_DETAIL)
    }

    companion object {
        private const val REQUEST_CODE_DETAIL = 6000
        fun newIntent(context: Context): Intent {
            return Intent(context, FavoritePostsActivity::class.java)
        }
    }
}