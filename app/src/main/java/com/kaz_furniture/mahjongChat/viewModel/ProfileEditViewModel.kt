package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.activity.ProfileEditActivity
import com.kaz_furniture.mahjongChat.data.User
import java.util.*

class ProfileEditViewModel: ViewModel() {
    val makeLogout = MutableLiveData<Boolean>()
    val editedName = MutableLiveData<String>()
    val editedIntroduction = MutableLiveData<String>()
    var presentUserId = ""
    var createdAtSnapshot = Date()
    var presentImageUrl = ""
    var followingUserIdsSnapshot = listOf<String>()
    var presentName = ""
    var presentIntroduction = ""

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

    fun loadUserInfo() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("userId", presentUserId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val myUser = it.result?.toObjects(User::class.java)
                        if (myUser != null && myUser.isNotEmpty()) {
                            followingUserIdsSnapshot = myUser[0].followingUserIds
                            presentImageUrl = myUser[0].imageUrl
                            createdAtSnapshot = myUser[0].createdAt
                            presentName = myUser[0].name
                            presentIntroduction = myUser[0].introduction
                        } else {
                            Toast.makeText(applicationContext, "認証エラーのため、ログアウトします", Toast.LENGTH_SHORT).show()
                            makeLogout.postValue(true)
                            return@addOnCompleteListener
                        }
                    }
                }
    }

    fun editUpload(activity: ProfileEditActivity) {
        val nameValue = editedName.value
        val introductionValue = editedIntroduction.value
        val user = User().apply {
            this.createdAt = createdAtSnapshot
            this.followingUserIds = followingUserIdsSnapshot
            this.imageUrl = presentImageUrl
            this.userId = presentUserId
            if (nameValue.isNullOrBlank() && introductionValue.isNullOrBlank()) {
                return
            }
            if (nameValue.isNullOrBlank()) {
                this.name = presentName
            } else this.name = nameValue

            if (introductionValue.isNullOrBlank()) {
                this.introduction = presentIntroduction
            } else this.introduction = introductionValue
        }


        FirebaseFirestore.getInstance()
                .collection("users")
                .document(presentUserId)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "SUCCESS", Toast.LENGTH_SHORT).show()
                        activity.setResult(Activity.RESULT_OK)
                        activity.finish()
                    } else {
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    }

                }
    }

}