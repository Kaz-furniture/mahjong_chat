package com.kaz_furniture.mahjongChat.viewModel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.activity.SplashActivity
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber

class LoginViewModel: ViewModel() {
    val makeLogout = MutableLiveData<Boolean>()
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
        } else if (password == null || password.isBlank() || password.length < 8) {
            passwordError.postValue(MahjongChatApplication.applicationContext.getString(R.string.inputCorrectly))
            false
        } else true
    }

    fun login(context: Context, activity: LoginActivity) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value ?:"", password.value ?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        SplashActivity.start(activity)
                    } else {
                        Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

//    private fun getToken() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
//            if (!task.isSuccessful) {
//                Toast.makeText(MahjongChatApplication.applicationContext, "GET_TOKEN_FAILED", Toast.LENGTH_SHORT).show()
//                tokenGetOK.postValue(true)
//                return@OnCompleteListener
//            }
//            Toast.makeText(MahjongChatApplication.applicationContext, "GET_TOKEN_SUCCESS", Toast.LENGTH_SHORT).show()
//            tokenGetOK.postValue(true)
//            val fcmResult = task.result ?:return@OnCompleteListener
//            fetchedToken = fcmResult
//        })
//    }

//    private fun getMyUser() {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?:return
//        FirebaseFirestore.getInstance()
//                .collection("users")
//                .whereEqualTo("userId", uid)
//                .get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful){
//                        val user = it.result?.toObjects(User::class.java)
//                        Timber.d("userList = $user")
//                        if (user != null && user.isNotEmpty()) {
//                            myUser = user[0]
//                            myUserOK.postValue(true)
//                        } else {
//                            Toast.makeText(applicationContext, "認証エラー", Toast.LENGTH_LONG).show()
//                            makeLogout.postValue(true)
//                            return@addOnCompleteListener
//                        }
//                    } else {
//                        Toast.makeText(applicationContext, "認証エラーです", Toast.LENGTH_LONG).show()
//                        makeLogout.postValue(true)
//                        return@addOnCompleteListener
//                    }
//                }
//    }

//    fun checkUserAndTokenGet() {
//        val myUserOKValue = myUserOK.value ?:false
//        val tokenGetOKValue = tokenGetOK.value ?:false
//        if (myUserOKValue && tokenGetOKValue) {
//            userAndTokenOK.postValue(true)
//        } else return
//    }

//    fun fCMTokenCheck() {
//        val presentToken = myUser.fcmToken
//        if (fetchedToken != presentToken) {
//            val newUser = myUser
//            newUser.fcmToken = fetchedToken
//            FirebaseFirestore.getInstance()
//                    .collection("users")
//                    .document(myUser.userId)
//                    .set(newUser)
//                    .addOnCompleteListener {
//                        if (it.isSuccessful){
//                            Toast.makeText(MahjongChatApplication.applicationContext, "TOKEN_UPDATE", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(MahjongChatApplication.applicationContext, "TOKEN_UPDATE_FAILED", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//        } else {
//            Toast.makeText(MahjongChatApplication.applicationContext, "TOKEN_KEEP", Toast.LENGTH_SHORT).show()
//        }
//    }
}