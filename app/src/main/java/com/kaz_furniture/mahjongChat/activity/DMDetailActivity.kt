package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.google.firebase.auth.FirebaseAuth
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
        if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrBlank()) launchLoginActivity()
        if (intent.getStringExtra(KEY_ID).isNullOrEmpty()) {
            val roomGet = (intent.getSerializableExtra(KEY_ROOM) as? DMRoom) ?: kotlin.run {
                Toast.makeText(this, "取得失敗", Toast.LENGTH_SHORT).show()
                finish()
                DMRoom()
            }
            viewModel.roomNow = roomGet.also { room ->
                viewModel.initData(room)
                title = allUserList.firstOrNull { it.userId == DMRoom.getOpponentUserId(room) }?.name ?:"NO_USER_NAME"
            }
        } else {
            viewModel.getRoom(intent.getStringExtra(KEY_ID) ?:return)
        }

        binding.message = viewModel.messageInput
        binding.submitButton.setOnClickListener {
            viewModel.sendMessage()
            binding.messageEditText.editableText.clear()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.initData(viewModel.roomNow)
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
        viewModel.idOK.observe(this, Observer {
            viewModel.initData(it)
            title = allUserList.firstOrNull { value -> value.userId == DMRoom.getOpponentUserId(it) }?.name ?:"NO_USER_NAME"
        })
        viewModel.opponentUserIdLiveData.observe(this, Observer {
            ProfileActivity.start(this, it)
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    companion object {
        private const val KEY_ROOM = "KEY_ROOM"
        private const val KEY_ID = "KEY_ID"
        fun newIntent(context: Context, room: DMRoom, id: String = ""): Intent {
            return Intent(context, DMDetailActivity::class.java).apply {
                putExtra(KEY_ROOM, room)
            }
        }
    }
}