package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber

class MainViewModel: ViewModel() {
    val updateData = MutableLiveData<Boolean>()
    var userName = "ゲスト"
    var uid: String = ""

    fun getName() {
        Timber.d("uid = $uid")
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val myUser = it.result?.toObjects(User::class.java)
                        if (myUser != null && myUser.isNotEmpty()) {
                            userName = myUser[0].name
                        } else {
                            return@addOnCompleteListener
                        }
                    } else {
                        return@addOnCompleteListener
                    }
                }
    }
}