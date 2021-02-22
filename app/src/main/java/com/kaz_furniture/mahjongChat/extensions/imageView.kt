package com.kaz_furniture.mahjongChat.extensions

import android.media.Image
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.GlideApp
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.Tile

//@BindingAdapter("userIcon")
//fun ImageView.setUserIcon(userId: String?) {
//    userId?.also {
//        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${it}/profileImage.jpg"))
//                .circleCrop()
//                .placeholder(R.drawable.loading_image)
//                .error(R.drawable.dog)
//                .into(this)
//    } ?: run {
//        GlideApp.with(this).load(R.drawable.dog).circleCrop().placeholder(R.drawable.loading_image).into(this)
//    }
//}

@BindingAdapter("iconOnImageId")
fun ImageView.setIconOnImageId(imageId: String?) {
    if (imageId.isNullOrBlank()) {
        GlideApp.with(this).load(R.drawable.dog).circleCrop().placeholder(R.drawable.loading_image).into(this)
    } else {
            GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(imageId))
                    .circleCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.dog)
                    .into(this)
    }
}

//@BindingAdapter("userIconNoCache")
//fun ImageView.setUserIconWithNoCache(userId: String?) {
//    userId?.also {
//        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child("${MahjongChatApplication.myUser.userId}/profileImage.jpg"))
//                .circleCrop()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .placeholder(R.drawable.loading_image)
//                .error(R.drawable.dog)
//                .into(this)
//    } ?: run {
//        GlideApp.with(this).load(R.drawable.dog).circleCrop().placeholder(R.drawable.loading_image).into(this)
//    }
//}

@BindingAdapter("postImage")
fun ImageView.setPostImage(post: Post) {
    val imageUrl = post.imageUrl
    if (imageUrl.isNullOrBlank() || post.postId.isBlank()) {
        setImageBitmap(null)
    } else {
        GlideApp.with(this).load(FirebaseStorage.getInstance().reference.child(imageUrl))
                .placeholder(R.drawable.loading_image)
                .into(this)
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