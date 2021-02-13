package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityPostDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class PostDetailActivity: BaseActivity() {
    lateinit var binding: ActivityPostDetailBinding
    private val viewModel: PostDetailViewModel by viewModels()
    lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail)
        binding.lifecycleOwner = this
        val postGet = (intent.getSerializableExtra(KEY) as? Post) ?:Post()
        post = postGet.also {
            viewModel.getChoices(it.postId)
            viewModel.getComments(it.postId)
            viewModel.getPost(it.postId)
            myUserCheck(it)
        }
        binding.userName.text = post.userName
        binding.userId = post.userId
        binding.post = post
        binding.explanation.text = post.explanation
        binding.createdTime.text = android.text.format.DateFormat.format(getString(R.string.time1), post.createdAt)
        binding.commentContent = viewModel.contentInput
        binding.submitButton.setOnClickListener {
            viewModel.submitComment(post.postId)
            binding.choiceCommentView.scrollToPosition(binding.choiceCommentView.customAdapter.itemCount - 1)
            binding.commentEditText.editableText.clear()
        }
        binding.userIcon.setOnClickListener {
            launchProfileActivity()
        }
        binding.userName.setOnClickListener {
            launchProfileActivity()
        }
        binding.more.setOnClickListener {
            PopupMenu(this, it).also { popupMenu ->
                popupMenu.menuInflater.inflate(R.menu.menu_post_detail_more, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.postEdit -> launchPostEditActivity()
                    }
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }
        binding.starNumberNumber.text = post.favoriteUserIds.size.toString()
        binding.starNumber = viewModel.starNumber.value
        binding.lightStar.setOnClickListener {
            viewModel.starClick(post)
            binding.lightStar.visibility = View.GONE
            binding.darkStar.visibility = View.VISIBLE
        }
        binding.darkStar.setOnClickListener {
            viewModel.starClick(post)
            binding.darkStar.visibility = View.GONE
            binding.lightStar.visibility = View.VISIBLE
        }
        viewModel.items.observe(this, Observer {
            binding.choiceCommentView.customAdapter.refresh(it)
        })
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = it
        })
        viewModel.updatedPost.observe(this, Observer {
            post = it
            myUserCheck(it)
            viewModel.starNumber.postValue(it.favoriteUserIds.size.toString())
        })
        viewModel.starNumber.observe(this, Observer {
            binding.starNumber = it
        })
    }

    private fun myUserCheck(post: Post) {
        when {
            post.userId == myUser.userId -> binding.more.visibility = View.VISIBLE
            post.userId != myUser.userId && post.favoriteUserIds.contains(myUser.userId) -> {
                binding.darkStar.visibility = View.GONE
                binding.lightStar.visibility = View.VISIBLE
            }
            else -> {
                binding.lightStar.visibility = View.GONE
                binding.darkStar.visibility = View.VISIBLE
            }
        }
    }

    private fun launchPostEditActivity() {
        Toast.makeText(this, "ACTIVITY", Toast.LENGTH_SHORT).show()
    }

    private fun launchProfileActivity() {
        val intent = ProfileActivity.newIntent(this, post.userId)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }

    companion object {
        private const val KEY = "KEY_POST"
        private const val REQUEST_CODE_PROFILE = 5000
        fun newIntent(context: Context, post: Post): Intent {
            return Intent(context, PostDetailActivity::class.java).apply {
                putExtra(KEY, post)
            }
        }
    }
}