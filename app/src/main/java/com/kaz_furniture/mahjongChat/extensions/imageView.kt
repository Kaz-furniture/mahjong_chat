package com.kaz_furniture.mahjongChat.extensions

import android.media.Image
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.GlideApp
import com.kaz_furniture.mahjongChat.data.Post

@BindingAdapter("userIcon")
fun ImageView.setUserIcon(userId: String?) {
    userId?.also {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${it}/profileImage.jpg")).circleCrop().into(this)
    } ?: run {
        setImageBitmap(null)
    }
}

@BindingAdapter("postImage")
fun ImageView.setPostImage(post: Post) {
    if (post.userId.isNullOrBlank() || post.postId.isBlank()) {
        setImageBitmap(null)
    } else {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${post.userId}/${post.postId}.jpg")).into(this)
    }
}