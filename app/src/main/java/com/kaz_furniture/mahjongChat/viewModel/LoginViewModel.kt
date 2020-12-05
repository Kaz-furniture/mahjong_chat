package com.kaz_furniture.mahjongChat.viewModel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity

class LoginViewModel: ViewModel() {

    val email = MutableLiveData<String>().apply {
        value = ""
    }
    val password = MutableLiveData<String>().apply {
        value = ""
    }
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(email) { result.value = submitValidation()}
        result.addSource(password) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.value ?:"").matches() && password.value?.isNotEmpty() == true
    }

    fun login(context: Context, activity: LoginActivity) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value ?:"", password.value ?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MainActivity.start(activity)
                    } else {
                        Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}