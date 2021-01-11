package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.view.PostView
import timber.log.Timber

class ProfileViewModel: ViewModel() {

    val item =MutableLiveData<MutableList<Post>>()
    var user = MutableLiveData<User>()
    val postSelected = MutableLiveData<Post>()
    val followChanged = MutableLiveData<Boolean>()

    fun getPostList(id: String) {
        val userPostList = ArrayList<Post>()
        userPostList.addAll(allPostList.filter { it.userId == id })
        item.postValue(userPostList)
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
    
    fun follow(userId: String) {
        val presentFollowList = ArrayList<String>()
        presentFollowList.apply {
            this.addAll(myUser.followingUserIds)
            this.add(userId)
        }
        myUser.followingUserIds = presentFollowList

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "FOLLOW_SUCCESS", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FOLLOW_FAILED", Toast.LENGTH_SHORT).show()
                }
        followChanged.postValue(true)
    }

    fun followCancel(userId: String) {
        val presentFollowList = ArrayList<String>()
        presentFollowList.apply {
            this.addAll(myUser.followingUserIds)
            this.remove(userId)
        }
        myUser.followingUserIds = presentFollowList

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "FOLLOW_SUCCESS", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FOLLOW_FAILED", Toast.LENGTH_SHORT).show()
                }
        followChanged.postValue(true)
    }
}