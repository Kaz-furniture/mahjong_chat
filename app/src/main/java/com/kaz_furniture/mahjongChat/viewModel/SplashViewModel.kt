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
        if (postsOKValue && usersOKValue && tokenCheckOKValue && timeCountValue) {
            allOK.postValue(true)
        } else return
    }

    fun fCMTokenCheck() {
        Timber.d("makeLogout: 7")
        val presentToken = myUser.fcmToken
        Timber.d("makeLogout: 8")
        if (fetchedToken != presentToken) {
            Timber.d("makeLogout: 10")
            val newUser = myUser.apply {
                fcmToken = fetchedToken
            }
            myUser = newUser
            Timber.d("makeLogout: 11")
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(myUser.userId)
                    .set(newUser)
                    .addOnCompleteListener {
                        Timber.d("makeLogout: 12")
                        if (it.isSuccessful){
                            tokenCheckOK.postValue(true)
                            Timber.d("makeLogout: 5")
                        } else {
                            Timber.d("makeLogout: 6")
                            tokenCheckOK.postValue(true)
                            Toast.makeText(applicationContext, "TOKEN_UPDATE_FAILED", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Timber.d("makeLogout: 9")
            tokenCheckOK.postValue(true)
        }
    }

    fun checkAccount() {
        Timber.d("makeLogout: 4")
        FirebaseAuth.getInstance().currentUser?.also {
            Timber.d("makeLogout: 3")
            getAllPosts()
            getAllUsers()
            getMyUser(it.uid)
            getFCMToken()
        } ?:run {
            getAllPosts()
            getAllUsers()
            tokenCheckOK.postValue(true)
//            Timber.d("makeLogout: 2")
//            makeLogout.postValue(true)
        }
    }

    private fun getMyUser(uid: String) {
        Timber.d("makeLogout: 20")
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userId", uid)
//                .orderBy(User::createdAt.name, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Timber.d("makeLogout: 21")
                        val user = it.result?.toObjects(User::class.java)
                        if (user != null && user.isNotEmpty()) {
                            myUser = user[0]
                            myUserOK.postValue(true)
                        } else {
                            Timber.d("makeLogout: 23")
                            makeLogout.postValue(true)
                            return@addOnCompleteListener
                        }
                    } else {
                        Timber.d("makeLogout: 22")
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
                        Timber.d("makeLogout: 30")
                        usersOK.postValue(true)
                        val result = it.result?.toObjects(User::class.java) ?: listOf()
                        allUserList.apply {
                            clear()
                            addAll(result)
                        }
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
                        Timber.d("makeLogout: 40")
                        postsOK.postValue(true)
                        val result = it.result?.toObjects(Post::class.java) ?: listOf()
                        allPostList.apply {
                            clear()
                            addAll(result)
                        }
                    } else {
                        postsOK.postValue(true)
                        Toast.makeText(applicationContext, "ALL_POST_FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

}