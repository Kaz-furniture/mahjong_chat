package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import timber.log.Timber
import java.io.ByteArrayOutputStream

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>()
    var usersName: String? = null
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    fun post(activity: PostActivity, binding: ActivityPostBinding) {
        val post = Post().apply {
            this.explanation = explanationInput.value
            this.userId = uId
            this.userName = usersName ?:getUserName()
        }

        val ref = FirebaseStorage.getInstance().reference.child("${FirebaseAuth.getInstance().currentUser?.uid ?:"noUser"}/${System.currentTimeMillis()}.jpg")
        val imageView = binding.postImageView
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val bAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bAOS)
        val data = bAOS.toByteArray()
        ref.putBytes(data)
            .addOnFailureListener{
                Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                bitmap.recycle()
            }
            .addOnSuccessListener {
                ref.downloadUrl.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        post.imageUrl = task.result.toString()
                        Timber.d("postImageUrl = ${post.imageUrl}")
                        Toast.makeText(applicationContext, "GET_URI_SUCCESS", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
                bitmap.recycle()
                FirebaseFirestore.getInstance()
                    .collection("posts")
                    .document(post.postId)
                    .set(post)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Timber.d("postImageUrl2 = ${post.imageUrl}")
                            Toast.makeText(applicationContext, "POST_SUCCESS", Toast.LENGTH_SHORT).show()
                            activity.setResult(Activity.RESULT_OK)
                            activity.finish()
//                        postComplete.postValue(true)
                        } else {
                            Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                        }
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