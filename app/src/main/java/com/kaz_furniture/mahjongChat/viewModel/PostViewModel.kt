package com.kaz_furniture.mahjongChat.viewModel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MediatorLiveData
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
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class PostViewModel: ViewModel() {
    val explanationInput = MutableLiveData<String>().apply {
        value = ""
    }
    val selectedTile = MutableLiveData<Tile>()
    private val imageOK = MutableLiveData<Boolean>().apply {
        value = false
    }
    private val timeForImageUrl = System.currentTimeMillis().toString()
    val postFinished = MutableLiveData<Boolean>()
    var selectedChoices = MutableLiveData<List<Choice>>()
    var tempChoice = Choice()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(explanationInput) { result.value = submitValidation()}
        result.addSource(selectedChoices) { result.value = submitValidation()}
        result.addSource(imageOK) { result.value = submitValidation()}
    }

    private fun submitValidation(): Boolean {
        val imageOKValue = imageOK.value ?:return false
        val choicesValue = selectedChoices.value ?:return false
        val explanationValue = explanationInput.value ?:return false
        return choicesValue.isNotEmpty() && explanationValue.isNotBlank() && imageOKValue
    }

    fun imageAdded() {
        imageOK.postValue(true)
    }

    fun post(data: ByteArray) {
        val post = Post().apply {
            explanation = explanationInput.value
            userId = myUser.userId
            imageUrl = "${myUser.userId}/${timeForImageUrl}.jpg"
        }
        val choicesList = selectedChoices.value

        if (choicesList != null && choicesList.isNotEmpty()) {
            for (value in choicesList) {
                value.postId = post.postId
                FirebaseFirestore.getInstance()
                        .collection("choices")
                        .document(value.choiceId)
                        .set(value)
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "CHOICES_FAILED", Toast.LENGTH_SHORT).show()
                        }
            }
        } else return

        FirebaseStorage.getInstance().reference.child("${myUser.userId}/${timeForImageUrl}.jpg")
                .putBytes(data)
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    postFinished.postValue(true)
                }

        FirebaseFirestore.getInstance()
            .collection("posts")
            .document(post.postId)
            .set(post)
            .addOnFailureListener {
                Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
            }
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