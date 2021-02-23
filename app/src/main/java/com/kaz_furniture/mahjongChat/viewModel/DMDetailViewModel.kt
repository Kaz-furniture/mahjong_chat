package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.DMMessage
import com.kaz_furniture.mahjongChat.data.DMRoom
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

    private fun submitValidation(): Boolean {
        val messageValue = messageInput.value
        return !messageValue.isNullOrBlank()
    }

    fun sendMessage() {
        val newMessage = DMMessage().apply {
            this.content = messageInput.value ?:""
            this.fromUserId = myUser.userId
            this.roomId = roomNow.roomId
        }
        FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .add(newMessage)
        updateRoom()
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
}