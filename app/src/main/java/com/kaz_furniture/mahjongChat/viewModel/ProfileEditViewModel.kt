package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.activity.ProfileEditActivity
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileEditViewModel: ViewModel() {
    val makeLogout = MutableLiveData<Boolean>()
    val editedName = MutableLiveData<String>()
    val editedIntroduction = MutableLiveData<String>()

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
                        activity.setResult(Activity.RESULT_OK)
                        activity.finish()
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }

                }
    }

}