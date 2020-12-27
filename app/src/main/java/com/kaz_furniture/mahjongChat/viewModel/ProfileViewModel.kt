package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.view.PostView

class ProfileViewModel: ViewModel() {

    val item =MutableLiveData<MutableList<Post>>()

    fun getPostList() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy(Post::createdAt.name, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(Post::class.java)
//                        item.value?.clear()
                        if (fetchedList == null || fetchedList.isEmpty()) {
                            Toast.makeText(MahjongChatApplication.applicationContext, "NO POST", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            item.postValue(fetchedList)
//                            item.value?.addAll(fetchedList)
//                            PostView.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}