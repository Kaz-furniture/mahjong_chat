package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.view.PostView
import timber.log.Timber

class ProfileViewModel: ViewModel() {

    val item =MutableLiveData<MutableList<Post>>()
    var user = MutableLiveData<User>()

    fun getPostList(id: String) {
        FirebaseFirestore.getInstance()
                .collection("posts")
//                .orderBy(Post::createdAt.name, Query.Direction.DESCENDING)
                .whereEqualTo("userId", id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fetchedList = task.result?.toObjects(Post::class.java)
                        if (fetchedList == null || fetchedList.isEmpty()) {
                            Toast.makeText(MahjongChatApplication.applicationContext, "NO POST", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            fetchedList.sortByDescending { it.createdAt }
                            item.postValue(fetchedList)
                        }
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun getUserInfo(userId: String) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(User::class.java) ?: kotlin.run {
                        Toast.makeText(applicationContext, "USER_INFO_FAILED", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    if (task.isSuccessful) {
                        user.postValue(result[0])
                    } else {
                        Toast.makeText(applicationContext, "NO_USER_INFO", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}