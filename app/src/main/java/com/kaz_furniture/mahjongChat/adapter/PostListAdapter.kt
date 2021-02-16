package com.kaz_furniture.mahjongChat.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ListEmptyFavoritesBinding
import com.kaz_furniture.mahjongChat.databinding.ListItemBinding
import com.kaz_furniture.mahjongChat.view.PostsViewInFavorites

class PostListAdapter (
        private val layoutInflater: LayoutInflater,
        private val postList: ArrayList<Post>,
        private val callback: Callback?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount(): Int {
                return if (postList.isNotEmpty()) postList.size else 1
        }

        interface Callback {
                fun openDetail(post: Post)
                fun openProfile(id: String?)
        }

        fun refresh(list: List<Post>) {
                postList.apply {
                        this.clear()
                        this.addAll(list)
                }
                notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
                return if (postList.isNotEmpty()) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                        VIEW_TYPE_EMPTY -> EmptyViewHolder(ListEmptyFavoritesBinding.inflate(layoutInflater, parent, false))
                        else -> ItemViewHolder(ListItemBinding.inflate(layoutInflater, parent, false), callback)
                }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when (holder) {
                        is EmptyViewHolder -> onBindViewHolder(holder)
                        is ItemViewHolder -> onBindViewHolder(holder, position)
                }
        }

        private fun onBindViewHolder(holder: EmptyViewHolder) {
                holder.binding.emptyText.text = applicationContext.getString(R.string.noFollower)
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                holder.bind(postList[position])
        }

        class ItemViewHolder(
                private val binding: ListItemBinding,
                private val callback: Callback?
        ): RecyclerView.ViewHolder(binding.root){
                fun bind(post: Post) {
                        binding.postUserName.text = post.userName
                        binding.explanation.text = post.explanation
                        binding.postItemImage.setOnClickListener {
                                callback?.openDetail(post)
                        }
                        binding.userIconImage.setOnClickListener {
                                callback?.openProfile(post.userId)
                        }
                        binding.postUserName.setOnClickListener {
                                callback?.openProfile(post.userId)
                        }
                        binding.starNumberNumber.text = post.favoriteUserIds.size.toString()
                        binding.createdTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time1), post.createdAt)
                        binding.post = post
                        binding.userId = post.userId
                }
        }
        class EmptyViewHolder(val binding: ListEmptyFavoritesBinding): RecyclerView.ViewHolder(binding.root)

        companion object {
                private const val VIEW_TYPE_ITEM = 0
                private const val VIEW_TYPE_EMPTY = 1
        }
}