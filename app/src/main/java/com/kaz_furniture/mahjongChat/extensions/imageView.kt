package com.kaz_furniture.mahjongChat.extensions

import android.media.Image
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.GlideApp
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.Tile

@BindingAdapter("userIcon")
fun ImageView.setUserIcon(userId: String?) {
    userId?.also {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${it}/profileImage.jpg")).circleCrop().into(this)
    } ?: run {
        GlideApp.with(this).load(R.drawable.dog).circleCrop().into(this)
    }
}

@BindingAdapter("postImage")
fun ImageView.setPostImage(post: Post) {
    if (post.userId.isBlank() || post.postId.isBlank()) {
        setImageBitmap(null)
    } else {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${post.userId}/${post.postId}.jpg")).into(this)
    }
}

@BindingAdapter("tileImage")
fun ImageView.tileImage(tile: Tile) {
    Glide.with(this).load(tile.imageId).into(this)
}

@BindingAdapter("choiceTileImage")
fun ImageView.choiceTileImage(choice: Choice?) {
    choice?.apply {
        Glide.with(this@choiceTileImage).load(choice.tileType.imageId).into(this@choiceTileImage)
    } ?: run {
        setImageBitmap(null)
    }
}