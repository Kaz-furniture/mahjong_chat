package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.databinding.*
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

        override fun getItemCount(): Int =
                if (items.isNotEmpty()) items.size else 1

        override fun getItemViewType(position: Int): Int {
            return if (items.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                VIEW_TYPE_EMPTY -> EmptyViewHolder(ListEmptyFavoritesBinding.inflate(LayoutInflater.from(context), parent, false))
                else -> ItemViewHolder(ListDmRoomBinding.inflate(LayoutInflater.from(context), parent, false))
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.userId = DMRoom.getOpponentUserId(data)
            holder.binding.userName = MahjongChatApplication.allUserList.filter { it.userId == DMRoom.getOpponentUserId(data) }[0].name
            holder.binding.roomView.setOnClickListener {
                viewModel.selectedDMRoom.postValue(data)
            }
            holder.binding.updatedTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.updatedTime), data.updatedAt)
            holder.binding.more.setOnClickListener {
                PopupMenu(it.context, it).also { popupMenu ->
                    popupMenu.menuInflater.inflate(R.menu.menu_dm_room, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when(menuItem.itemId) {
                            R.id.delete -> viewModel.deleteRoom(data)
                        }
                        return@setOnMenuItemClickListener true
                    }
                }.show()
            }
        }

        class ItemViewHolder(val binding: ListDmRoomBinding): RecyclerView.ViewHolder(binding.root)
        class EmptyViewHolder(val binding: ListEmptyFavoritesBinding): RecyclerView.ViewHolder(binding.root) {
            init {
                binding.emptyText.setText(R.string.noDMRoom)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}