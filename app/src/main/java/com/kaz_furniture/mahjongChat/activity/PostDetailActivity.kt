package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityPostDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class PostDetailActivity: BaseActivity() {
    lateinit var binding: ActivityPostDetailBinding
    private val viewModel: PostDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail)
        binding.lifecycleOwner = this
        val postGet = (intent.getSerializableExtra(KEY) as? Post) ?:Post()
        val post = postGet.also {
            viewModel.getChoices(it.postId)
            viewModel.getComments(it.postId)
        }
        binding.userName.text = post.userName
        binding.userId = post.userId
        binding.post = post
        binding.explanation.text = post.explanation
        binding.createdTime.text = android.text.format.DateFormat.format(getString(R.string.time1), post.createdAt)
        binding.commentContent = viewModel.contentInput
        binding.submitButton.setOnClickListener {
            viewModel.submitComment(post.postId)
        }
        viewModel.choicesList.observe(this, Observer {
            binding.choicesView.customAdapter.refresh(it)
        })
        viewModel.commentsList.observe(this, Observer {
            binding.commentsView.customAdapterComment.refresh(it)
        })
    }

    companion object {
        private const val KEY = "KEY_POST"
        fun newIntent(context: Context, post: Post): Intent {
            return Intent(context, PostDetailActivity::class.java).apply {
                putExtra(KEY, post)
            }
        }
    }
}