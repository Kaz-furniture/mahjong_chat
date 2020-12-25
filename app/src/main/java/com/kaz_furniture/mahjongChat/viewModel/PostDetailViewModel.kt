package com.kaz_furniture.mahjongChat.viewModel

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityPostDetailBinding

class PostDetailViewModel: ViewModel() {

    fun setImage(post: Post, binding: ActivityPostDetailBinding) {
        val storageRef = FirebaseStorage.getInstance().reference
        val postImageRef = storageRef.child("${post.userId}/${post.postId}.jpg")
        Glide.with(applicationContext)
            .load(postImageRef)
            .into(binding.postImageView)
        val iconImage = storageRef.child("${post.userId}/profileImage.jpg")
        Glide.with(applicationContext)
            .load(iconImage)
            .into(binding.userIcon)
    }

}