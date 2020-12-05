package com.kaz_furniture.mahjongChat.viewModel

import android.util.Patterns
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateAccountViewModel: ViewModel() {
    val name = MutableLiveData<String>().apply {
        value = ""
    }
    val email = MutableLiveData<String>().apply {
        value = ""
    }
    val password = MutableLiveData<String>().apply {
        value = ""
    }
    val passwordValidate = MutableLiveData<String>().apply {
        value = ""
    }
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(name) { result.value = submitValidation()}
        result.addSource(email) { result.value = submitValidation()}
        result.addSource(password) { result.value = submitValidation()}
        result.addSource(passwordValidate) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        return name.value?.isNotEmpty() == true && validateEmail() && validatePassword()
    }

    private fun validateEmail() = Patterns.EMAIL_ADDRESS.matcher(email.value ?:"").matches()
    private fun validatePassword(): Boolean {
        return password.value == passwordValidate.value
    }
}