package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityPostDetailBinding
import com.kaz_furniture.mahjongChat.databinding.DialogDeleteConfirmBinding
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class PostDetailActivity: BaseActivity() {
    lateinit var binding: ActivityPostDetailBinding
    private val viewModel: PostDetailViewModel by viewModels()
    lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_detail)
        binding.lifecycleOwner = this
        val postGet = (intent.getSerializableExtra(KEY) as? Post) ?: kotlin.run {
            finish()
            Toast.makeText(this, "情報取得できませんでした", Toast.LENGTH_SHORT).show()
            return@run Post()
        }
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
            showPopup(it)
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
            binding.explanation.text = post.explanation
            viewModel.starNumber.postValue(it.favoriteUserIds.size.toString())
        })
        viewModel.starNumber.observe(this, Observer {
            binding.starNumber = it
        })
    }

    private fun showPopup(view: View) {
        PopupMenu(this, view).also { popupMenu ->
            popupMenu.menuInflater.inflate(R.menu.menu_post_detail_more, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.postEdit -> launchPostEditActivity()
                    R.id.postDelete -> showDeleteDialog()
                }
                return@setOnMenuItemClickListener true
            }
        }.show()
    }

    private fun showDeleteDialog() {
        MaterialDialog(this).show {
            title = getString(R.string.deleteConfirm)
            val binding = DialogDeleteConfirmBinding.inflate(LayoutInflater.from(this@PostDetailActivity), null, false)
            binding.apply {
                yesButton.setOnClickListener {
                    dismiss()
                    viewModel.deletePost(post)
                    finish()
                }
                cancelButton.setOnClickListener {
                    dismiss()
                }
            }
            setContentView(binding.root)
        }
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
        val intent = PostEditActivity.newIntent(this, post, viewModel.choices as ArrayList<Choice>)
        startActivityForResult(intent, REQUEST_CODE_POST_EDIT)
    }

    private fun launchProfileActivity() {
        val intent = ProfileActivity.newIntent(this, post.userId)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "画像の更新にはアプリの再起動が必要です", Toast.LENGTH_LONG).show()
        }
        if (requestCode == REQUEST_CODE_POST_EDIT) {
            viewModel.getChoices(post.postId)
            viewModel.getComments(post.postId)
            viewModel.getPost(post.postId)
            myUserCheck(post)
        }
    }

    companion object {
        private const val KEY = "KEY_POST"
        private const val REQUEST_CODE_PROFILE = 5000
        private const val REQUEST_CODE_POST_EDIT = 5500
        fun newIntent(context: Context, post: Post): Intent {
            return Intent(context, PostDetailActivity::class.java).apply {
                putExtra(KEY, post)
            }
        }
    }
}