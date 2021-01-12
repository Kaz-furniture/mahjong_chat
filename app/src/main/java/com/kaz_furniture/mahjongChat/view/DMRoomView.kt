package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.databinding.ListCommentBinding
import com.kaz_furniture.mahjongChat.databinding.ListDmRoomBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel

class DMRoomView: RecyclerView {

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
        private val items = mutableListOf<DMRoom>()

        fun refresh(list: List<DMRoom>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_cell, null, false))
                ItemViewHolder(ListDmRoomBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder is ItemViewHolder)
                onBindViewHolder(holder, position)
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.userId = DMRoom.getOpponentUserId(data, myUser.userId)
            holder.binding.userName = MahjongChatApplication.allUserList.filter { it.userId == DMRoom.getOpponentUserId(data, myUser.userId) }[0].name
            holder.binding.roomView.setOnClickListener {
                viewModel.selectedDMRoom.postValue(data)
            }
        }

        class ItemViewHolder(val binding: ListDmRoomBinding): RecyclerView.ViewHolder(binding.root)
    }
}