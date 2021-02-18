package com.kaz_furniture.mahjongChat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ListEmptyFavoritesBinding
import com.kaz_furniture.mahjongChat.databinding.ListItemProfileBinding
import com.kaz_furniture.mahjongChat.viewModel.FavoritePostsViewModel

class PostsViewInFavorites: RecyclerView {

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

        private val viewModel: FavoritePostsViewModel by(context as ComponentActivity).viewModels()
        private val items = mutableListOf<Post>()

        fun refresh(list: List<Post>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return if (items.isNotEmpty()) items.size else 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (items.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
                when (viewType) {
                    VIEW_TYPE_EMPTY -> EmptyViewHolder(ListEmptyFavoritesBinding.inflate(LayoutInflater.from(context), parent, false))
                    else -> ItemViewHolder(ListItemProfileBinding.inflate(LayoutInflater.from(context), parent, false))
                }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when(holder) {
                is ItemViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.binding.post = data
            holder.binding.userId = data.userId
            holder.binding.postItemImage.setOnClickListener {
                viewModel.postSelected.postValue(data)
            }
        }

        class EmptyViewHolder(val binding: ListEmptyFavoritesBinding): RecyclerView.ViewHolder(binding.root)
        class ItemViewHolder(val binding: ListItemProfileBinding): RecyclerView.ViewHolder(binding.root)
        companion object {
            private const val VIEW_TYPE_EMPTY = 0
            private const val VIEW_TYPE_ITEM = 1
        }
    }
}