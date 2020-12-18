package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityPostDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class PostDetailActivity: BaseActivity() {
//    lateinit var binding: ActivityPostDetailBinding
//    private val viewModel: PostDetailViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail)
//        binding.lifecycleOwner = this
//        val post = (intent.getSerializableExtra(KEY) as? Post) ?:return
//        binding.userName.text = post.userName
//
//    }
//
//    companion object {
//        private const val KEY = "KEY_POST"
//        fun newIntent(context: Context, post: Post): Intent {
//            return Intent(context, PostDetailActivity::class.java).apply {
//                putExtra(KEY, post)
//            }
//        }
//    }
}