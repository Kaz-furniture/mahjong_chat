package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration

class DMDetailViewModel: ViewModel() {

    private var listenerRegistration: ListenerRegistration? = null


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}