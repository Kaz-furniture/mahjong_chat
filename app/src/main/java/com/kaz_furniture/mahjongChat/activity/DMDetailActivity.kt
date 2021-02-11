package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityDmDetailBinding
import com.kaz_furniture.mahjongChat.view.DMMessageView
import com.kaz_furniture.mahjongChat.viewModel.DMDetailViewModel
import timber.log.Timber

class DMDetailActivity: BaseActivity() {
    private val viewModel: DMDetailViewModel by viewModels()
    lateinit var binding: ActivityDmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dm_detail)
        binding.lifecycleOwner = this
        val roomGet = (intent.getSerializableExtra(KEY_ROOM) as? DMRoom) ?: kotlin.run {
            Toast.makeText(this, "NO_DM_INFO", Toast.LENGTH_SHORT).show()
            finish()
            DMRoom()
        }
        viewModel.roomNow = roomGet.also {
            viewModel.initData(it)
        }
        binding.message = viewModel.messageInput
        binding.submitButton.setOnClickListener {
            viewModel.sendMessage()
            binding.messageEditText.editableText.clear()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.initData(roomGet)
        }
        binding.messageEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
        viewModel.messagesList.observe(this, Observer {
            binding.messagesView.apply {
                customAdapter.refresh(it)
                scrollToPosition(customAdapter.itemCount - 1)
            }
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.canSend.observe(this, Observer {
            binding.canSubmit = it
        })
        title = allUserList.firstOrNull { it.userId == DMRoom.getOpponentUserId(roomGet) }?.name ?:"NO_USER_NAME"
    }


    companion object {
        private const val KEY_ROOM = "KEY_ROOM"
        fun newIntent(context: Context, room: DMRoom): Intent {
            return Intent(context, DMDetailActivity::class.java).apply {
                putExtra(KEY_ROOM, room)
            }
        }
    }
}