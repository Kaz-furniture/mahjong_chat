package com.kaz_furniture.mahjongChat.viewModel

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.LoginActivity
import com.kaz_furniture.mahjongChat.activity.MainActivity
import com.kaz_furniture.mahjongChat.activity.SplashActivity
import com.kaz_furniture.mahjongChat.data.User
import timber.log.Timber

class SplashViewModel: ViewModel() {

    private val splashTime = 6000L
    var makeLogout = MutableLiveData<Boolean>()
    var fcmToken = ""

    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                return@OnCompleteListener
            }
            Toast.makeText(applicationContext, "TOKEN_SUCCESS", Toast.LENGTH_SHORT).show()
            fcmToken = task.result ?:return@OnCompleteListener
        })
    }

    fun checkAccount(activity: SplashActivity) {
        FirebaseAuth.getInstance().currentUser?.also {
            getMyUser(it.uid)
            startMainActivity(activity)
        } ?:run {
            startLoginActivity(activity)
        }
    }

    private fun getMyUser(uid: String) {
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
                            myUser.userId = uid
                            myUser.name = user[0].name
                            myUser.createdAt = user[0].createdAt
                            myUser.followingUserIds = user[0].followingUserIds
                            myUser.imageUrl = user[0].imageUrl
                            myUser.introduction = user[0].introduction
                            myUser.deletedAt = user[0].deletedAt
                        } else {
//                            Toast.makeText(MahjongChatApplication.applicationContext, "認証エラーのためログアウトします", Toast.LENGTH_SHORT).show()
                            makeLogout.postValue(true)
                            return@addOnCompleteListener
                        }
                    } else {
                        return@addOnCompleteListener
                    }
                }
    }

    private fun startMainActivity(activity: SplashActivity) {
        Handler(Looper.getMainLooper()).postDelayed({
            MainActivity.start(activity)
        }, splashTime)
    }

    private fun startLoginActivity(activity: SplashActivity){
        Handler(Looper.getMainLooper()).postDelayed({
            LoginActivity.start(activity)
        }, splashTime)
    }
}