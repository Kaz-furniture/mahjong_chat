package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Post

class FavoritePostsViewModel: ViewModel() {
    val favoritesList = MutableLiveData<List<Post>>()
    val postSelected = MutableLiveData<Post>()

    fun updatePosts() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(Post::class.java)
                        allPostList.apply {
                            this.clear()
                            this.addAll(fetchedList ?: listOf())
                            favoritesList.postValue(this.filter { value -> value.favoriteUserIds.contains(myUser.userId) })
                        }
                    }
                }
    }
}