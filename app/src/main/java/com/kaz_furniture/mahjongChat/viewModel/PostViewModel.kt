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

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>()
//    val postComplete = MutableLiveData<Boolean>()

    fun post(activity: PostActivity) {
        val post = Post().apply {
            this.explanation = explanationInput.value
            this.userId = FirebaseAuth.getInstance().currentUser?.uid
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
}