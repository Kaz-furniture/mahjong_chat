package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Notification
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.extensions.sendFcm
import com.kaz_furniture.mahjongChat.view.PostView
import timber.log.Timber

class ProfileViewModel: ViewModel() {

    val item =MutableLiveData<MutableList<Post>>()
    var user = MutableLiveData<User>()
    val postSelected = MutableLiveData<Post>()
    val followChanged = MutableLiveData<Boolean>()

    fun getPostList(id: String) {
        val userPostList = ArrayList<Post>()
        userPostList.addAll(allPostList.filter { it.userId == id && it.deletedAt == null })
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
                        user.postValue(result.firstOrNull() ?:User())
                    } else {
                        Toast.makeText(applicationContext, "NO_USER_INFO", Toast.LENGTH_SHORT).show()
                    }
                }
    }
    
    fun follow(userId: String) {
        allUserList.removeAll { it.userId == myUser.userId }
        val presentFollowList = ArrayList<String>()
        presentFollowList.apply {
            this.addAll(myUser.followingUserIds)
            this.add(userId)
        }
        myUser.apply {
            followingUserIds = presentFollowList
            allUserList.add(this)
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnCompleteListener {
                    sendFcm(
                        allUserList.firstOrNull { it.userId == userId } ?:return@addOnCompleteListener,
                        TYPE_FOLLOWED,
                        applicationContext.getString(R.string.followedTitle, myUser.name),
                        "",
                        myUser.userId,
                    )
                    createNotification(userId)
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FOLLOW_FAILED", Toast.LENGTH_SHORT).show()
                }
        followChanged.postValue(true)
    }

    private fun createNotification(userId: String) {
        val newNotification = Notification().apply {
            this.content = ""
            this.fromUserId = myUser.userId
            this.toUserId = userId
            this.type = TYPE_FOLLOWED
            this.contentId = myUser.userId
        }

        FirebaseFirestore.getInstance().collection("notifications")
            .document(newNotification.notificationId)
            .set(newNotification)
    }

    fun followCancel(userId: String) {
        allUserList.removeAll { it.userId == myUser.userId }
        val presentFollowList = ArrayList<String>()
        presentFollowList.apply {
            this.addAll(myUser.followingUserIds)
            this.remove(userId)
        }
        myUser.apply {
            followingUserIds = presentFollowList
            allUserList.add(this)
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FOLLOW_FAILED", Toast.LENGTH_SHORT).show()
                }
        followChanged.postValue(true)
    }

    companion object {
        private const val TYPE_FOLLOWED = 1
    }
}