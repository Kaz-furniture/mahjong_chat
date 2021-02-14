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
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.PostActivity
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.TextImageData
import com.kaz_furniture.mahjongChat.data.Tile
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import timber.log.Timber
import java.io.ByteArrayOutputStream

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>()
    val selectedTile = MutableLiveData<Tile>()
    val selectedOK = MutableLiveData<Boolean>()

    var selectedChoices = MutableLiveData<List<Choice>>()
    var tempChoice = Choice()

    fun post(activity: PostActivity, binding: ActivityPostBinding) {
        val post = Post().apply {
            this.explanation = explanationInput.value
            this.userId = myUser.userId
            this.userName = myUser.name
            this.imageUrl = "${myUser.userId}/${postId}.jpg"
        }
        val choicesList = selectedChoices.value

        if (choicesList != null && choicesList.isNotEmpty()) {
            for ((index, value) in choicesList.withIndex()) {
                value.postId = post.postId
                FirebaseFirestore.getInstance()
                        .collection("choices")
                        .document(value.choiceId)
                        .set(value)
                        .addOnCompleteListener {
                            Toast.makeText(applicationContext, "CHOICE_UPLOAD_$index", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "CHOICES_FAILED", Toast.LENGTH_SHORT).show()
                        }
            }
        } else return

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
                        Toast.makeText(applicationContext, "UPLOAD_IMAGE_SUCCESS", Toast.LENGTH_SHORT).show()
                bitmap.recycle()
            }

        FirebaseFirestore.getInstance()
            .collection("posts")
            .document(post.postId)
            .set(post)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "POST_SUCCESS", Toast.LENGTH_SHORT).show()
                    activity.setResult(Activity.RESULT_OK)
                    activity.finish()
                } else {
                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                }
            }

        FirebaseFirestore.getInstance()
                .collection("choices")
                .document()
    }

    fun selectTile(tile: Tile) {
        selectedTile.postValue(tile)
        tempChoice.tileType = tile
    }

    fun setWay(index: Int) {
        tempChoice.way = index
        setChoice()
    }

    fun deleteChoice(deleteChoice: Choice) {
        val list = mutableListOf<Choice>().apply {
            selectedChoices.value?.also {
                if (it.isNotEmpty())
                    addAll(it)
            }
        }
        list.remove(deleteChoice)
        selectedChoices.postValue(list)
    }

    private fun setChoice() {
        val list = mutableListOf<Choice>().apply {
            selectedChoices.value?.also {
                if (it.isNotEmpty())
                    addAll(it)
            }
        }
        list.add(Choice().apply {
            way = tempChoice.way
            tileType = tempChoice.tileType
        })
        tempChoice = Choice()
        selectedChoices.postValue(list)
        Timber.d("imageId = ${list[0].tileType.imageId}")
    }

//    var choiceData = ChoiceData()
//    class ChoiceData: BaseObservable() {
//        @Bindable
//        var choiceName = "選択してください"
//            set(value) {
//                field = value
//                notifyPropertyChanged(BR.choiceName)
//            }
//    }
}