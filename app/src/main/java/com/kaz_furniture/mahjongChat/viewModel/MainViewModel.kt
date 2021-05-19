package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.data.Notification
import com.kaz_furniture.mahjongChat.data.Post
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel: ViewModel() {
    val updateData = MutableLiveData<Boolean>()
    val dMRoomList = MutableLiveData<List<DMRoom>>()
    val selectedDMRoom = MutableLiveData<DMRoom?>()
    val userSelected = MutableLiveData<String>()
    val updatedList = MutableLiveData<List<Post>>()
    val notificationsLiveData = MutableLiveData<List<Notification>>()

    fun selectRoomPostValue(room: DMRoom) {
        selectedDMRoom.postValue(room)
    }

    fun clearSelect() {
        selectedDMRoom.postValue(null)
    }

    fun createDMRoom(userId: String) {
        val newRoom = DMRoom().apply {
            userIds = listOf(myUser.userId, userId)
        }
        FirebaseFirestore.getInstance()
                .collection("DMRoom")
                .document(newRoom.roomId)
                .set(newRoom)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    val newRoomList = ArrayList<DMRoom>().apply {
                        this.add(newRoom)
                        this.addAll(dMRoomList.value ?: listOf())
                    }
                    dMRoomList.postValue(newRoomList)
                }
    }

    fun loadPostList() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fetchedList = it.result?.toObjects(Post::class.java) ?: listOf()
                        allPostList.clear()
                        allPostList.addAll(fetchedList)
                        updatedList.postValue(fetchedList.filter { value -> value.deletedAt == null })
                    } else {
                        Toast.makeText(MahjongChatApplication.applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun getDMRooms() {
        FirebaseFirestore.getInstance()
                .collection("DMRoom")
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val result = task.result?.toObjects(DMRoom::class.java) ?: kotlin.run {
                            Toast.makeText(applicationContext, "NO_DM", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        val myDMList = ArrayList<DMRoom>()
                        myDMList.addAll(result.filter { it.userIds.contains(myUser.userId) && it.deletedAt == null})
                        dMRoomList.postValue(myDMList)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "DM_FAILED", Toast.LENGTH_SHORT).show()
                }
    }

    fun deleteRoom(room: DMRoom) {
        val newRoom = room.apply {
            this.deletedAt = Date()
        }
        FirebaseFirestore.getInstance()
                .collection("DMRoom")
                .document(newRoom.roomId)
                .set(newRoom)
                .addOnCompleteListener {
                    val newRoomList = ArrayList<DMRoom>().apply {
                        this.addAll(dMRoomList.value ?: listOf())
                        this.remove(room)
                    }
                    dMRoomList.postValue(newRoomList)
                }
    }

    fun fetchNotifications() {
        FirebaseFirestore.getInstance().collection("notifications")
            .whereEqualTo("toUserId", myUser.userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObjects(Notification::class.java) ?: listOf()
                    val orderList = result.sortedByDescending { it.submitTime }
                    notificationsLiveData.postValue(orderList)
                }
            }
    }
}