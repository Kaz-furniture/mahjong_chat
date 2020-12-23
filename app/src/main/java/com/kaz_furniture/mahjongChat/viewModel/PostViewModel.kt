package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import timber.log.Timber
import java.io.ByteArrayOutputStream

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>()

    fun post(activity: PostActivity, binding: ActivityPostBinding) {
        val post = Post().apply {
            this.explanation = explanationInput.value
            this.userId = myUser.userId
            this.userName = myUser.name
            this.imageUrl = "${myUser.userId}/${postId}.jpg"
        }

        val ref = FirebaseStorage.getInstance().reference.child("${myUser.userId}/${post.postId}.jpg")
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
//                ref.downloadUrl.addOnCompleteListener {task ->
//                    if (task.isSuccessful) {
//                        post.imageUrl = task.result.toString()
//                        Timber.d("postImageUrl = ${post.imageUrl}")
                        Toast.makeText(applicationContext, "UPLOAD_IMAGE_SUCCESS", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
//                    }
//                }
                bitmap.recycle()
            }
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

    fun selectChoice(name: String) {
        choiceData.choiceName = name
    }

    var choiceData = ChoiceData()
    class ChoiceData: BaseObservable() {
        @Bindable
        var choiceName = "選択してください"
            set(value) {
                field = value
                notifyPropertyChanged(BR.choiceName)
            }
    }
}