package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.ProfileEditActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ProfileEditViewModel: ViewModel() {
    val makeLogout = MutableLiveData<Boolean>()
    val editedName = MutableLiveData<String>()
    val editedIntroduction = MutableLiveData<String>()
    private val updatePostList = ArrayList<Post>()
    var image: Bitmap? = null
    val imageBoolean = MutableLiveData<Boolean>()
    var uCropSrcUriLive = MutableLiveData<Uri>()

    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(editedName) { result.value = submitValidation()}
        result.addSource(editedIntroduction) { result.value = submitValidation()}
        result.addSource(imageBoolean) { result.value = submitValidation()}
    }

    fun showProfileImage(binding: ActivityProfileEditBinding) {
        uCropSrcUriLive.postValue(myUser.imageUrl.toUri())
        val storageRef = FirebaseStorage.getInstance().reference
        val postImageRef = storageRef.child(myUser.imageUrl)
        Glide.with(applicationContext)
            .load(postImageRef)
            .into(binding.roundedImageView)
    }

    fun uCropStart(data: Intent, binding: ActivityProfileEditBinding) {
        val resultUri = UCrop.getOutput(data)
        uCropSrcUriLive.value = resultUri
        val cropSrc = uCropSrcUriLive.value ?:return
        val inputStream = applicationContext.contentResolver.openInputStream(cropSrc)
        image = BitmapFactory.decodeStream(inputStream)
//        val bitmapImage = image ?:return
//        image = Bitmap.createScaledBitmap(bitmapImage, 200, 200, true)
        val imageView = binding.roundedImageView
        imageView.setImageBitmap(image)
    }

    private fun submitValidation(): Boolean {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        return if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank() && image == null) {
            false
        } else if (!nameValue.isNullOrBlank() && nameValue.length >20 ) {
            false
        } else !(!introductionValue.isNullOrBlank() && introductionValue.length >100)
    }

    fun editUpload(activity: ProfileEditActivity) {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        val user = User().apply {
            this.createdAt = myUser.createdAt
            this.followingUserIds = myUser.followingUserIds
            this.imageUrl = myUser.imageUrl
            this.userId = myUser.userId
            if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank() && image == null) {
                return
            }
            if (nameValue.isNullOrBlank()) {
                this.name = myUser.name
            } else this.name = nameValue

            if (introductionValue.isNullOrBlank()) {
                this.introduction = myUser.introduction
            } else this.introduction = introductionValue
        }

        if (image != null) {
            val ref = FirebaseStorage.getInstance().reference.child("${myUser.userId}/profileImage.jpg")
            val bAOS = ByteArrayOutputStream()
            image?.compress(Bitmap.CompressFormat.JPEG, 60, bAOS)
            val data = bAOS.toByteArray()
            ref.putBytes(data)
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "UPLOAD_ICON_SUCCESS", Toast.LENGTH_SHORT).show()
                    }

            Glide.get(applicationContext).clearMemory()
            CoroutineScope(Dispatchers.IO).launch {
                Glide.get(applicationContext).clearDiskCache()
            }
        }

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