package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>()
    var usersName: String? = null
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    fun post(activity: PostActivity) {
        val post = Post().apply {
            this.explanation = explanationInput.value
            this.userId = uId
            this.userName = usersName ?:getUserName()
            this.imageUrl = "http://test.com"
        }
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.postId)
                .set(post)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_SHORT).show()
                        activity.setResult(Activity.RESULT_OK)
                        activity.finish()
//                        postComplete.postValue(true)
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun getUserName(): String {
        var newUserName = "ゲスト"
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("userId", uId)
                    .limit(1)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val myUser = it.result?.toObjects(User::class.java)
                            if (myUser != null && myUser.isNotEmpty()) {
                                 newUserName = myUser[0].name
                            } else {
                                return@addOnCompleteListener
                            }
                        } else {
                            return@addOnCompleteListener
                        }
                    }
        return newUserName
    }
}