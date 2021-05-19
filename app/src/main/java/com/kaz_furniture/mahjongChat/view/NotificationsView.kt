package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Notification
import com.kaz_furniture.mahjongChat.databinding.ListCommentBinding
import com.kaz_furniture.mahjongChat.databinding.ListEmptyFavoritesBinding
import com.kaz_furniture.mahjongChat.databinding.ListFollowUserBinding
import com.kaz_furniture.mahjongChat.databinding.ListNotificationsBinding
import com.kaz_furniture.mahjongChat.viewModel.FollowDisplayViewModel
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class NotificationsView: RecyclerView {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
    }

    class Adapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewModel: MainViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<Notification>()

        init {
        }

        fun refresh(list: List<Notification>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = if (items.isNotEmpty()) items.size else 1

        override fun getItemViewType(position: Int): Int {
            return if (items.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            when (viewType) {
                VIEW_TYPE_EMPTY -> EmptyViewHolder(ListEmptyFavoritesBinding.inflate(LayoutInflater.from(context), parent, false))
                else -> ItemViewHolder(ListNotificationsBinding.inflate(LayoutInflater.from(context), parent, false))
            }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.apply {
                imageId = allUserList.firstOrNull { it.userId == data.fromUserId }?.imageUrl ?:""
                content = data.content
                userName = allUserList.firstOrNull { it.userId == data.fromUserId }?.name ?:""
                commentTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time2), data.submitTime)
            }
        }

        class ItemViewHolder(val binding: ListNotificationsBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListEmptyFavoritesBinding): RecyclerView.ViewHolder(binding.root) {
            init {
                binding.emptyText.apply {
                    setText(R.string.noNotification)
                    textSize = 16F
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}