package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.adapter.DMListAdapter
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber

class MainViewModel: ViewModel() {
    val updateData = MutableLiveData<Boolean>()
    val makeLogout = MutableLiveData<Boolean>()
    var dMToUserId = MutableLiveData<String>()
    var dMContent = MutableLiveData<String>()
    var dMToUserName = MutableLiveData<String>()
    val dMList = ArrayList<DMRoom>()

    fun loadPostList(postList: ArrayList<Post>, adapter: PostListAdapter) {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy(Post::createdAt.name, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(Post::class.java)
                        postList.clear()
                        if (fetchedList == null || fetchedList.isEmpty()) {
                            Toast.makeText(MahjongChatApplication.applicationContext, "NO POST", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            postList.addAll(fetchedList)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun loadDMUsers(dMList: ArrayList<DMRoom>) {
        FirebaseFirestore.getInstance()
                .collection("DM")
                .orderBy(DMRoom::createdAt.name, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(DMRoom::class.java)
                        dMList.clear()
                        if (fetchedList == null || fetchedList.isEmpty()) {
                            Toast.makeText(MahjongChatApplication.applicationContext, "NO DM", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        } else {
                            dMList.addAll(fetchedList)
                        }
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun sendDM() {
        val dM = DMRoom().apply {
            this.content = dMContent.value ?:""
            this.fromUserId = myUser.userId
            this.toUserId = dMToUserId.value ?:""
            this.toUserName = dMToUserName.value ?:""
            this.fromUserName = myUser.name
        }
        FirebaseFirestore.getInstance()
                .collection("DM")
                .document(dM.roomId)
                .set(dM)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }

    }

    fun dMUserNameSet(dMUserNameList: ArrayList<String>, adapter: DMListAdapter) {
//        loadDMUsers(dMList)
        val toUserNameList = dMList.map { it.toUserName }
        val toUserNameSet = toUserNameList.toSet()
        dMUserNameList.clear()
        dMUserNameList.addAll(toUserNameSet.toList())
        adapter.notifyDataSetChanged()
    }
}