package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.GlideApp
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allUserList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.ProfileEditActivity
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import com.kaz_furniture.mahjongChat.extensions.setIconOnImageId
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ProfileEditViewModel: ViewModel() {
    val editedName = MutableLiveData<String>().apply {
        value = myUser.name
    }
    val editedIntroduction = MutableLiveData<String>().apply {
        value = myUser.introduction
    }
    var image: Bitmap? = null
    private val imageBoolean = MutableLiveData<Boolean>()
    var uCropSrcUriLive = MutableLiveData<Uri>()
    val updateOK = MutableLiveData<Boolean>()
    private val timeForImageUrl = System.currentTimeMillis().toString()

    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(editedName) { result.value = submitValidation()}
        result.addSource(editedIntroduction) { result.value = submitValidation()}
        result.addSource(imageBoolean) { result.value = submitValidation()}
    }


    fun showProfileImage(binding: ActivityProfileEditBinding) {
        uCropSrcUriLive.postValue(myUser.imageUrl.toUri())
//        GlideApp.with(applicationContext).load(FirebaseStorage.getInstance().reference.child("${myUser.userId}/profileImage.jpg"))
//            .circleCrop().into(binding.roundedImageView)
        binding.roundedImageView.setIconOnImageId(myUser.imageUrl)
    }

    fun uCropStart(data: Intent, binding: ActivityProfileEditBinding) {
        val resultUri = UCrop.getOutput(data)
        uCropSrcUriLive.postValue(resultUri)
        GlideApp.with(applicationContext).load(resultUri).circleCrop().into(binding.roundedImageView)
        imageBoolean.postValue(true)
    }

    private fun submitValidation(): Boolean {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        return if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank() && imageBoolean.value == null) {
            false
        } else if (!nameValue.isNullOrBlank() && nameValue.length >20 ) {
            false
        } else !(!introductionValue.isNullOrBlank() && introductionValue.length >100)
    }

    fun editUpload() {
        allUserList.removeAll { it.userId == myUser.userId }
        val exImageId = myUser.imageUrl
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        val user = myUser.apply {
            if (imageBoolean.value != null) {
                imageUrl = "${myUser.userId}/${timeForImageUrl}.jpg"
            }
            if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank() && imageBoolean.value == null) {
                return
            }
            if (nameValue.isNullOrBlank()) {
                name = myUser.name
            } else this.name = nameValue

            if (introductionValue.isNullOrBlank()) {
                this.introduction = myUser.introduction
            } else this.introduction = introductionValue
        }
        allUserList.add(user)

        if (imageBoolean.value != null) {
            val ref = FirebaseStorage.getInstance().reference.child("${myUser.userId}/${timeForImageUrl}.jpg")
            val bAOS = ByteArrayOutputStream()
            val cropSrc = uCropSrcUriLive.value ?:return
            val inputStream = applicationContext.contentResolver.openInputStream(cropSrc)
            image = BitmapFactory.decodeStream(inputStream)
            image?.compress(Bitmap.CompressFormat.JPEG, 60, bAOS)
            val data = bAOS.toByteArray()
            ref.putBytes(data)
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        updateOK.postValue(true)
                    }
            Timber.d("exImageId = $exImageId")
            if (exImageId.isNotBlank()) {
                FirebaseStorage.getInstance().reference.child(exImageId).delete().addOnFailureListener {
                    Toast.makeText(applicationContext, "FAILED_IN_DELETE", Toast.LENGTH_SHORT).show()
                }
            }
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(myUser.userId)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        myUser = user
//                        postFetch()
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }

                }
    }

//    private fun postFetch() {
//        val filteredList = allPostList.filter { it.userId == myUser.userId }
//        myPostList.addAll(filteredList)
//        postUpdate()
//    }

//    private fun postUpdate() {
//
//        for (value in myPostList) {
//            val nameValue = editedName.value
//
//            if (nameValue.isNullOrBlank()) {
//                value.userName = myUser.name
//            } else value.userName = nameValue
//
//            FirebaseFirestore.getInstance()
//                    .collection("posts")
//                    .document(value.postId)
//                    .set(value)
//        }
//        updateOK.postValue(true)
//    }

}