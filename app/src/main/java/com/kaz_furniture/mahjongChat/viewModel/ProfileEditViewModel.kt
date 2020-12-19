package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.ProfileEditActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ProfileEditViewModel: ViewModel() {
    val makeLogout = MutableLiveData<Boolean>()
    val editedName = MutableLiveData<String>()
    val editedIntroduction = MutableLiveData<String>()
    private val updatePostList = ArrayList<Post>()

    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(editedName) { result.value = submitValidation()}
        result.addSource(editedIntroduction) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        return if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank()) {
            false
        } else if (!nameValue.isNullOrBlank() && nameValue.length >20 ) {
            false
        } else !(!introductionValue.isNullOrBlank() && introductionValue.length >100)
    }

    fun editUpload(activity: ProfileEditActivity, binding: ActivityProfileEditBinding) {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        val user = User().apply {
            this.createdAt = myUser.createdAt
            this.followingUserIds = myUser.followingUserIds
            this.imageUrl = myUser.imageUrl
            this.userId = myUser.userId
            if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank()) {
                return
            }
            if (nameValue.isNullOrBlank()) {
                this.name = myUser.name
            } else this.name = nameValue

            if (introductionValue.isNullOrBlank()) {
                this.introduction = myUser.introduction
            } else this.introduction = introductionValue

        }

//        val ref = FirebaseStorage.getInstance().reference.child("${FirebaseAuth.getInstance().currentUser?.uid ?:"noUser"}/${user.userId}.jpg")
//        val imageView = binding.roundedImageView
//        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//        val bAOS = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bAOS)
//        val data = bAOS.toByteArray()
//        ref.putBytes(data)
//                .addOnFailureListener{
//                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
//                    bitmap.recycle()
//                }
//                .addOnSuccessListener {
//                    Toast.makeText(applicationContext, "UPLOAD_IMAGE_SUCCESS", Toast.LENGTH_SHORT).show()
//                    bitmap.recycle()
//                }


        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_SHORT).show()
                        myUser = user
                        postFetch(activity)
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }

                }
    }

    private fun postFetch(activity: ProfileEditActivity) {

        FirebaseFirestore.getInstance()
                .collection("posts")
                .whereEqualTo("userId", myUser.userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fetchedPosts = task.result?.toObjects(Post::class.java) ?:return@addOnCompleteListener
                        updatePostList.clear()
                        Timber.d("InPostUpdate ${fetchedPosts.size}")
                        updatePostList.addAll(fetchedPosts)
                        postUpdate(activity)
                    }
                }
    }

    private fun postUpdate(activity: ProfileEditActivity) {

        for (value in updatePostList) {
            val nameValue = editedName.value

            if (nameValue.isNullOrBlank()) {
                value.userName = myUser.name
            } else value.userName = nameValue

            FirebaseFirestore.getInstance()
                    .collection("posts")
                    .document(value.postId)
                    .set(value)
        }
        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

}