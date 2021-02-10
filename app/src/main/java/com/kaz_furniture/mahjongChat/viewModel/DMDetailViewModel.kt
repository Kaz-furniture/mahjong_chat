package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.DMMessage
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class DMDetailViewModel: ViewModel() {
    val messagesList = MutableLiveData<List<DMMessage>>()
    private var messageListener: ListenerRegistration? = null
    var roomIdNow = ""
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
            this.roomId = roomIdNow
        }
        FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .add(newMessage)
    }

    fun initData(roomId: String) {
        FirebaseFirestore.getInstance()
                .collection("DMMessage")
                .whereEqualTo("roomId", roomId)
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
                .whereEqualTo("roomId", roomIdNow)
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

    override fun onCleared() {
        super.onCleared()
        messageListener?.remove()
    }
}