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
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.data.DMMessage
import com.kaz_furniture.mahjongChat.databinding.*
import com.kaz_furniture.mahjongChat.viewModel.DMDetailViewModel
import com.kaz_furniture.mahjongChat.viewModel.PostDetailViewModel
import java.text.DateFormat

class DMMessageView: RecyclerView {

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

        private val viewModel: DMDetailViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<DMMessage>()

        fun refresh(list: List<DMMessage>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        override fun getItemViewType(position: Int): Int {
            return if (items[position].fromUserId == myUser.userId) {
                VIEW_TYPE_MINE
            } else {
                VIEW_TYPE_OPPONENT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_cell, null, false))
                when (viewType) {
                    VIEW_TYPE_OPPONENT -> OpponentViewHolder(ListDmChatBinding.inflate(LayoutInflater.from(context), parent, false))
                    else -> MineViewHolder(ListDmMessageMineBinding.inflate(LayoutInflater.from(context), parent, false))
                }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (holder) {
                is OpponentViewHolder -> onBindViewHolder(holder, position)
                is MineViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: OpponentViewHolder, position: Int) {
            val data = items[position]
            holder.binding.apply {
                this.dateTextView.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time2), data.createdAt)
                this.messageTextView.text = data.content
                this.userId = data.fromUserId
            }
        }

        private fun onBindViewHolder(holder: MineViewHolder, position: Int) {
            val data = items[position]
            holder.binding.apply {
                this.dateTextView.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time2), data.createdAt)
                this.messageTextView.text = data.content
            }
        }


        class OpponentViewHolder(val binding: ListDmChatBinding): RecyclerView.ViewHolder(binding.root)
        class MineViewHolder(val binding: ListDmMessageMineBinding): RecyclerView.ViewHolder(binding.root)

        companion object {
            private const val VIEW_TYPE_OPPONENT = 0
            private const val VIEW_TYPE_MINE = 1
        }
    }
}