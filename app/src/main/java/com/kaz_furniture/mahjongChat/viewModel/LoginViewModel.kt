package com.kaz_furniture.mahjongChat.viewModel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity

class LoginViewModel: ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(email) { result.value = submitValidation()}
        result.addSource(password) { result.value = submitValidation()}
    }
    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()

    private fun submitValidation(): Boolean {
        val email = email.value
        val password = password.value
        return if (email == null || email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailError.postValue(MahjongChatApplication.applicationContext.getString(R.string.inputCorrectly))
            false
        }
        else if (password == null || password.isBlank() || password.length < 8) {
            passwordError.postValue(MahjongChatApplication.applicationContext.getString(R.string.inputCorrectly))
            false
        }
        else{
            true
        }
    }

    fun login(context: Context, activity: LoginActivity) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value ?:"", password.value ?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val id = FirebaseAuth.getInstance().currentUser?.uid ?:return@addOnCompleteListener
                        MainActivity.start(activity, id)
                    } else {
                        Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}