package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.DMMessage
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.data.Notification
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.extensions.sendFcm
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DMDetailViewModel: ViewModel() {
    val messagesList = MutableLiveData<List<DMMessage>>()
    private var messageListener: ListenerRegistration? = null
    var roomNow = DMRoom()
    val messageInput = MutableLiveData<String>()
    val canSend = MediatorLiveData<Boolean>().also { result ->
        result.addSource(messageInput) { result.value = submitValidation()}
    }
    val idOK = MutableLiveData<DMRoom>()

    private fun submitValidation(): Boolean {
        val messageValue = messageInput.value
        return !messageValue.isNullOrBlank()
    }

    fun getRoom(id: String) {
        FirebaseFirestore.getInstance().collection("DMRoom")
            .document(id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObject(DMRoom::class.java) ?:return@addOnCompleteListener
                    roomNow = result
                    idOK.postValue(result)
                }
            }
    }

    fun sendMessage() {
        val newMessage = DMMessage().apply {
            this.content = messageInput.value ?:""
            this.fromUserId = myUser.userId
            this.roomId = roomNow.roomId
        }
        Timber.d("messageInput2 = ${messageInput.value}")
        FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .add(newMessage)
                .addOnCompleteListener {
                    allUserList.firstOrNull { value -> value.userId == DMRoom.getOpponentUserId(roomNow) }?.apply {
                        Timber.d("messageInput = ${newMessage.content}")
                        sendFcm(this,
                            TYPE_DM_MESSAGE,
                            applicationContext.getString(R.string.dMNotifyTitle, myUser.name),
                            newMessage.content,
                            newMessage.roomId
                        )
                        createNotification(this, newMessage.content, newMessage.roomId)
                    }
                }
        updateRoom()
    }

    private fun createNotification(user: User, content: String, roomId: String) {
        val newNotification = Notification().apply {
            this.content = content
            this.fromUserId = myUser.userId
            this.toUserId = user.userId
            this.type = TYPE_DM_MESSAGE
            this.contentId = roomId
        }

        FirebaseFirestore.getInstance().collection("notifications")
            .document(newNotification.notificationId)
            .set(newNotification)
    }

    fun initData(room: DMRoom) {
        FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .whereEqualTo("roomId", room.roomId)
                .orderBy("createdAt")
                .get()
                .addOnCompleteListener {
                    var date = Date()
                    it.result?.toObjects(DMMessage::class.java)?.also { messages->
                        messagesList.postValue(messages)
                        date = messages.lastOrNull()?.createdAt ?: Date()
                    }
                    initSubscribe(date)
                }
    }

    private fun initSubscribe(lastCreatedAt: Date) {
        messageListener = FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .whereEqualTo("roomId", roomNow.roomId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereGreaterThan("createdAt", lastCreatedAt)
                .limit(1L)
                .addSnapshotListener { snapshot, firebaseFirestoreException ->
                    Timber.d("initSubscribe snapshot:$snapshot firebaseFirestoreException:$firebaseFirestoreException")
                    if (firebaseFirestoreException != null) {
                        firebaseFirestoreException.printStackTrace()
                        return@addSnapshotListener
                    }
                    snapshot?.toObjects(DMMessage::class.java)?.firstOrNull()?.also {
                        val messages = ArrayList<DMMessage>().apply {
                            this.addAll(messagesList.value ?: listOf())
                            this.add(it)
                        }
                        messagesList.postValue(messages)
                    }
                }
    }

    private fun updateRoom() {
        val newRoom = roomNow.apply {
            this.updatedAt = Date()
        }
        FirebaseFirestore.getInstance()
                .collection("DMRoom")
                .document(roomNow.roomId)
                .set(newRoom)
    }

    override fun onCleared() {
        super.onCleared()
        messageListener?.remove()
    }

    companion object {
        private const val TYPE_DM_MESSAGE = 0
    }
}