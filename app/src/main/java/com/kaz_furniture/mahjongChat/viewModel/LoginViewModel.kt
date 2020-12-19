package com.kaz_furniture.mahjongChat.viewModel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber

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
                        getMyUser()
                        MainActivity.start(activity)
                    } else {
                        Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun getMyUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?:return
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userId", uid)
//                .orderBy(User::createdAt.name, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val user = it.result?.toObjects(User::class.java)
                        Timber.d("userList = $user")
                        if (user != null && user.isNotEmpty()) {
                            MahjongChatApplication.myUser.userId = uid
                            MahjongChatApplication.myUser.name = user[0].name
                            MahjongChatApplication.myUser.createdAt = user[0].createdAt
                            MahjongChatApplication.myUser.followingUserIds = user[0].followingUserIds
                            MahjongChatApplication.myUser.imageUrl = user[0].imageUrl
                            MahjongChatApplication.myUser.introduction = user[0].introduction
                            MahjongChatApplication.myUser.deletedAt = user[0].deletedAt
                        } else {
//                            Toast.makeText(MahjongChatApplication.applicationContext, "認証エラーのためログアウトします", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                    } else {
                        return@addOnCompleteListener
                    }
                }
    }
}