package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.firestore.ListenerRegistration
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityDmDetailBinding
import com.kaz_furniture.mahjongChat.viewModel.DMDetailViewModel

class DMDetailActivity: BaseActivity() {
    private val viewModel: DMDetailViewModel by viewModels()
    lateinit var binding: ActivityDmDetailBinding
    lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dm_detail)
        binding.lifecycleOwner = this
        roomId = intent.getStringExtra("KEY_ROOM_ID") ?: kotlin.run {
            Toast.makeText(this, "NO_DM_INFO", Toast.LENGTH_SHORT).show()
            finish()
            ""
        }
        viewModel.roomIdNow = roomId.also {
            viewModel.initData(it)
        }
        binding.message = viewModel.messageInput
        binding.submitButton.setOnClickListener {
            viewModel.sendMessage()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.initData(roomId)
        }

        viewModel.messagesList.observe(this, Observer {
            binding.messagesView.customAdapter.refresh(it)
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.canSend.observe(this, Observer {
            binding.canSubmit = it
        })
    }


    companion object {
        private const val KEY = "KEY_ROOM_ID"
        fun newIntent(context: Context, roomId: String): Intent {
            return Intent(context, DMDetailActivity::class.java).apply {
                putExtra(KEY, roomId)
            }
        }
    }
}