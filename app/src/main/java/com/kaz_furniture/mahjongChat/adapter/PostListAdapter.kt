package com.kaz_furniture.mahjongChat.adapter

import android.app.Application
import android.telecom.Call
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ListItemBinding

class PostListAdapter (
        private val layoutInflater: LayoutInflater,
        private val postList: ArrayList<Post>,
//        private val callback: Callback?
): RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
                return postList.size
        }

//        interface Callback {
//                fun openDetail(post: Post)
//        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
//        private val callback: Callback?
): RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post) {
                binding.postUserName.text = post.userName
                binding.explanation.text = post.explanation
//                binding.postItemImage.setOnClickListener {
//                        callback?.openDetail(post)
//                }
                binding.createdTime.text = android.text.format.DateFormat.format(applicationContext.getString(R.string.time1), post.createdAt)
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("${post.userId}/${post.postId}.jpg")
                Glide.with(applicationContext)
                        .load(imageRef)
                        .into(binding.postItemImage)
        }
}

}