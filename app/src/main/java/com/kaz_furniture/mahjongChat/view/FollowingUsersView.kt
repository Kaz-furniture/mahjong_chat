package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.protobuf.Empty
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ListCommentBinding
import com.kaz_furniture.mahjongChat.databinding.ListDmRoomBinding
import com.kaz_furniture.mahjongChat.databinding.ListEmptyFavoritesBinding
import com.kaz_furniture.mahjongChat.databinding.ListFollowingUsersBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class FollowingUsersView: RecyclerView {

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
        private val items = mutableListOf<String>()

        fun refresh(list: List<String>) {
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
                    else -> ItemViewHolder(ListFollowingUsersBinding.inflate(LayoutInflater.from(context), parent, false))
                }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.imageId = allUserList.firstOrNull { it.userId == data }?.imageUrl ?:""
            holder.binding.userName = allUserList.firstOrNull { it.userId == data }?.name ?:""
            holder.binding.roomView.setOnClickListener {
                viewModel.userSelected.postValue(data)
            }
        }

        class ItemViewHolder(val binding: ListFollowingUsersBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListEmptyFavoritesBinding): RecyclerView.ViewHolder(binding.root) {
            init {
                binding.emptyText.apply {
                    setText(R.string.noFollowUser)
                    textSize = 14F
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}