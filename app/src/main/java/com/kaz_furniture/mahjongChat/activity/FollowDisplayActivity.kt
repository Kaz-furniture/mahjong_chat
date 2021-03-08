package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.firestore.auth.User
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityFollowDisplayBinding
import com.kaz_furniture.mahjongChat.viewModel.FollowDisplayViewModel

class FollowDisplayActivity: BaseActivity() {
    private val viewModel: FollowDisplayViewModel by viewModels()
    lateinit var binding: ActivityFollowDisplayBinding
    lateinit var userId: String
    var followOrFollower = FOLLOWER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_display)
        binding.lifecycleOwner = this
        viewModel.updateAllUsers()
        userId = intent.getStringExtra(keyUserId) ?: kotlin.run {
            finish()
            return@run ""
        }
        followOrFollower = intent.getIntExtra(keyInt, FOLLOWER)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.updateAllUsers()
            binding.swipeRefresh.isRefreshing = false
        }
        viewModel.myUserUpdated.observe(this, Observer {
            viewModel.updateAllUsers()
        })
        viewModel.updatedUsers.observe(this, Observer {
            binding.followUsersView.customAdapter.refresh(checkAndConvert(it))
        })
        viewModel.profileOpenLiveData.observe(this, Observer {
            launchProfileActivity(it)
        })
        title = if (followOrFollower == FOLLOWER) getString(R.string.followerJapanese)
            else getString(R.string.followJapanese)
    }

    private fun checkAndConvert(list: List<com.kaz_furniture.mahjongChat.data.User>): List<String> {
        return if (followOrFollower == FOLLOW) {
            list.firstOrNull { it.userId == userId }?.followingUserIds ?: listOf<String>()
        } else {
            list.filter { it.followingUserIds.contains(userId) }.map { it.userId }
        }
    }

    private fun launchProfileActivity(userId: String) {
        val intent = ProfileActivity.newIntent(this, userId)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }


    companion object {
        private const val REQUEST_CODE_PROFILE = 9500
        private const val keyInt = "KEY_INT"
        private const val keyUserId = "KEY_USER_ID"
        private const val FOLLOWER = 0
        private const val FOLLOW = 1
        fun newIntent(context: Context, number: Int, userId: String): Intent {
            return Intent(context, FollowDisplayActivity::class.java).apply {
                putExtra(keyInt, number)
                putExtra(keyUserId, userId)
            }
        }
    }
}