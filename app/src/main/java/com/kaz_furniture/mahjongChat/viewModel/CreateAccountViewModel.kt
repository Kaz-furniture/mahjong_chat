package com.kaz_furniture.mahjongChat.viewModel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.CreateAccountActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber
import java.util.*

class CreateAccountViewModel: ViewModel() {
    val nameInput = MutableLiveData<String>()
    val nameError = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordValidate = MutableLiveData<String>()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(nameInput) { result.value = submitValidation()}
        result.addSource(email) { result.value = submitValidation()}
        result.addSource(password) { result.value = submitValidation()}
        result.addSource(passwordValidate) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        return validateName() && validateEmail() && validatePassword()
    }

    private fun validateName(): Boolean {
        val nameValue = nameInput.value
        return if (nameValue == null || nameValue.isBlank()) {
            Timber.d("nameInput = $nameValue")
            nameError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val emailValue = email.value
        return if (emailValue == null || emailValue.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
            emailError.postValue(applicationContext.getString(R.string.inputCorrectly))
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        val passwordValue = password.value
        return if (passwordValue == null || passwordValue.isBlank() || passwordValue != passwordValidate.value || passwordValue.length < 8) {
            passwordError.postValue(applicationContext.getString(R.string.inputCorrectly))
            Timber.d("password = $passwordValue")
            Timber.d("passwordValidate = ${passwordValidate.value}")
            false
        } else {
            true
        }
    }


    fun createAuthUser(activity: CreateAccountActivity) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value ?:return, password.value ?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseAuth.getInstance().currentUser?.uid?.also {
                            createUser(it, activity)
                            return@addOnCompleteListener
                        }

                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun createUser(uid: String, activity: CreateAccountActivity) {
        val user = User().apply {
            userId = uid
            name = nameInput.value ?:""
            createdAt = Date()
        }
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                        MainActivity.start(activity, uid)
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}