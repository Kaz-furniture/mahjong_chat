package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileBinding
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
        buttonTypeCheck()
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPostList(userId)
        }
        binding.userId = userId
        binding.userButton.setOnClickListener {
            buttonClick()
        }
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
        viewModel.followChanged.observe(this, Observer {
            buttonTypeCheck()
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun buttonTypeCheck() {
        when {
            userId == myUser.userId -> binding.buttonText = getString(R.string.profileEdit)
            myUser.followingUserIds.contains(userId) -> binding.buttonText = getString(R.string.following)
            else -> binding.buttonText = getString(R.string.follow)
        }
    }

    private fun buttonClick() {
        when {
            userId == myUser.userId -> launchProfileEditActivity()
            myUser.followingUserIds.contains(userId) -> viewModel.followCancel(userId)
            else -> viewModel.follow(userId)
        }
    }

    private fun launchProfileEditActivity() {
        val intent = ProfileEditActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT)
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
        private const val REQUEST_CODE_PROFILE_EDIT = 1050
        private const val KEY = "KEY_ID"
        fun newIntent(context: Context, id: String?): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY, id)
            }
        }
    }
}