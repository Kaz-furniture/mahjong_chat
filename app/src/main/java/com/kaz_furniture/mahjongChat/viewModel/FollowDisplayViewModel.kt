package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.User

class FollowDisplayViewModel: ViewModel() {
    val updatedUsers = MutableLiveData<List<User>>()
    val myUserUpdated = MutableLiveData<Boolean>()
    val profileOpenLiveData = MutableLiveData<String>()

    fun updateAllUsers() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val users = it.result?.toObjects(User::class.java)
                        if (users != null && users.isNotEmpty()) {
                            allUserList.apply {
                                clear()
                                addAll(users)
                            }
                            myUser = allUserList.firstOrNull { value ->  value.userId == myUser.userId } ?:return@addOnCompleteListener
                            updatedUsers.postValue(users)
                        } else {
                            Toast.makeText(applicationContext, "USERS_GET_FAILED", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                    } else {
                        Toast.makeText(applicationContext, "USERS_GET_FAILED", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
    }

    fun buttonClick(userId: String) {
        val newFollowingUsersList = ArrayList<String>().apply {
            addAll(myUser.followingUserIds)
            if (myUser.followingUserIds.contains(userId)) remove(userId)
            else add(userId)
        }
        myUser.followingUserIds = newFollowingUsersList

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(myUser)
                .addOnCompleteListener {
                    myUserUpdated.postValue(true)
                }
    }

    fun profileOpen(userId: String) {
        profileOpenLiveData.postValue(userId)
    }

}