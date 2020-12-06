package com.kaz_furniture.mahjongChat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ListItemBinding

class PostListAdapter (
        private val layoutInflater: LayoutInflater,
        private val postList: ArrayList<Post>
): RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
                return postList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
                val binding = DataBindingUtil.inflate<ListItemBinding>(
                        layoutInflater,
                        R.layout.list_item,
                        parent,
                        false
                )
                return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bind(postList[position])
        }

class ViewHolder(
        private val binding: ListItemBinding
): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post) {
                binding.postId.text = post.postId
        }
}

}