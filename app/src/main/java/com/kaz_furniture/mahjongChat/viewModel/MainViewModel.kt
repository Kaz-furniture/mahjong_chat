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
    val profileOpenLiveData = MutableLiveData<String>()
    val postDetailOpenLiveData = MutableLiveData<Post>()


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
//                    val type0List = result.filter { it.type == TYPE_DM_MESSAGE }
                    val output0List = arrayListOf<Notification>()
                    result.filter { it.type == TYPE_DM_MESSAGE }.sortedByDescending { it.submitTime }.map { it.fromUserId }.toSet().forEach { value ->
                        output0List.add(result.filter { it.type == TYPE_DM_MESSAGE }.sortedByDescending { it.submitTime }.firstOrNull { it.fromUserId == value } ?:return@forEach)
                    }
                    val output1List = result.filter { it.type == TYPE_FOLLOWED }
                    val output2List = result.filter { it.type == TYPE_FAVORITE }
                    val finalList = arrayListOf<Notification>().apply {
                        addAll(output0List)
                        addAll(output1List)
                        addAll(output2List)
                    }
                    notificationsLiveData.postValue(finalList.sortedByDescending { it.submitTime })
                }
            }
    }

    fun notificationClicked(type: Int, id: String = "") {
        when (type) {
            TYPE_DM_MESSAGE -> {
                Timber.d("notificationClicked1 $id")
                getAndPostRoom(id)
            }

            TYPE_FOLLOWED -> {
                profileOpenLiveData.postValue(id)
            }

            TYPE_FAVORITE -> {
                val post = allPostList.firstOrNull { it.postId == id } ?:return
                postDetailOpenLiveData.postValue(post)
            }

            else -> {
                return
            }
        }
    }

    private fun getAndPostRoom(id: String) {
        FirebaseFirestore.getInstance().collection("DMRoom")
            .document(id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObject(DMRoom::class.java) ?:return@addOnCompleteListener
                    selectedDMRoom.postValue(result)
                    Timber.d("notificationClicked3")
                }
            }
    }

    companion object {
        private const val TYPE_DM_MESSAGE = 0
        private const val TYPE_FOLLOWED = 1
        private const val TYPE_FAVORITE = 2
    }
}