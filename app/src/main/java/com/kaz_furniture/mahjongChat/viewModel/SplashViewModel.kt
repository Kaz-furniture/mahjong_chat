package com.kaz_furniture.mahjongChat.viewModel

import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.logging.Handler

class SplashViewModel: ViewModel() {

    var makeLogout = MutableLiveData<Boolean>()
    var fetchedToken: String = ""
    val myUserOK = MutableLiveData<Boolean>()
    val tokenCheckOK = MutableLiveData<Boolean>()
    val tokenGetOK = MutableLiveData<Boolean>()
    val postsOK = MutableLiveData<Boolean>()
    val usersOK = MutableLiveData<Boolean>()
    val userAndTokenOK = MutableLiveData<Boolean>()
    val allOK = MutableLiveData<Boolean>()
    val timeCount = MutableLiveData<Boolean>()

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, "GET_TOKEN_FAILED", Toast.LENGTH_SHORT).show()
                return@OnCompleteListener
            }
            Toast.makeText(applicationContext, "GET_TOKEN_SUCCESS", Toast.LENGTH_SHORT).show()
            tokenGetOK.postValue(true)
            val fcmResult = task.result ?:return@OnCompleteListener
            fetchedToken = fcmResult
        })
    }

    fun checkUserAndTokenGet() {
        val myUserOKValue = myUserOK.value ?:false
        val tokenGetOKValue = tokenGetOK.value ?:false
        if (myUserOKValue && tokenGetOKValue) {
            userAndTokenOK.postValue(true)
        } else return
    }

    fun checkAllFinished() {
        val postsOKValue = postsOK.value ?:false
        val usersOKValue = usersOK.value ?:false
        val tokenCheckOKValue = tokenCheckOK.value ?:false
        val timeCountValue = timeCount.value ?:false
        if (postsOKValue && usersOKValue && tokenCheckOKValue &&timeCountValue) {
            allOK.postValue(true)
        } else return
    }

    fun fCMTokenCheck() {
        val presentToken = myUser.fcmToken
        if (fetchedToken != presentToken) {
            val newUser = myUser
            newUser.fcmToken = fetchedToken
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(myUser.userId)
                    .set(newUser)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            tokenCheckOK.postValue(true)
                            Toast.makeText(applicationContext, "TOKEN_UPDATE", Toast.LENGTH_SHORT).show()
                        } else {
                            tokenCheckOK.postValue(true)
                            Toast.makeText(applicationContext, "TOKEN_UPDATE_FAILED", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            tokenCheckOK.postValue(true)
            Toast.makeText(applicationContext, "TOKEN_KEEP", Toast.LENGTH_SHORT).show()
            Timber.d("TokenKeep")
        }
    }

    fun checkAccount() {
        FirebaseAuth.getInstance().currentUser?.also {
            getAllPosts()
            getAllUsers()
            getMyUser(it.uid)
            getFCMToken()
        } ?:run {
            makeLogout.postValue(true)
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
                        if (user != null && user.isNotEmpty()) {
                            myUser = user[0]
                            myUserOK.postValue(true)
                        } else {
                            makeLogout.postValue(true)
                            return@addOnCompleteListener
                        }
                    } else {
                        makeLogout.postValue(true)
                        return@addOnCompleteListener
                    }
                }
    }

    private fun getAllUsers() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        usersOK.postValue(true)
                        val result = it.result?.toObjects(User::class.java) ?: listOf()
                        allUserList.addAll(result)
                        Toast.makeText(applicationContext, "ALL_USER_SUCCESS", Toast.LENGTH_SHORT).show()
                    } else {
                        usersOK.postValue(true)
                        Toast.makeText(applicationContext, "ALL_USER_FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun getAllPosts() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        postsOK.postValue(true)
                        val result = it.result?.toObjects(Post::class.java) ?: listOf()
                        allPostList.addAll(result)
                        Toast.makeText(applicationContext, "ALL_POST_SUCCESS", Toast.LENGTH_SHORT).show()
                    } else {
                        postsOK.postValue(true)
                        Toast.makeText(applicationContext, "ALL_POST_FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun clearCache() {
        Glide.get(applicationContext).clearMemory()
        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(applicationContext).clearDiskCache()
        }
    }

}