package com.kaz_furniture.mahjongChat.viewModel


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.Tile
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class PostEditViewModel: ViewModel() {
    val selectedTile = MutableLiveData<Tile>()
    var tempChoice = Choice()
    var selectedChoices = MutableLiveData<List<Choice>>()
    val explanationInput = MutableLiveData<String>()
    var savedChoices = listOf<Choice>()
    private val timeForImageUrl = System.currentTimeMillis().toString()
    var imageOK = false
    val imageUploaded = MutableLiveData<Boolean>()

    fun postUpdate(post: Post) {
        val newPost = post.apply {
            updatedAt = Date()
            if (imageOK) {
                imageUrl = "${myUser.userId}/${timeForImageUrl}.jpg"
            }
            if (!explanationInput.value.isNullOrBlank()) {
                explanation = explanationInput.value
            }
        }
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.postId)
                .set(newPost)
    }

    fun postImageUpload(bitmap: Bitmap) {
        val ref = FirebaseStorage.getInstance().reference.child("${MahjongChatApplication.myUser.userId}/${timeForImageUrl}.jpg")
        val bAOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bAOS)
        val data = bAOS.toByteArray()
        ref.putBytes(data)
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
                    bitmap.recycle()
                }
                .addOnSuccessListener {
                    imageUploaded.postValue(true)
                    bitmap.recycle()
                }
    }

    fun choicesUpdate(postId: String) {
        val selectedChoicesValue = ArrayList<Choice>().apply {
            this.addAll(selectedChoices.value ?:return)
        }
        savedChoices.forEach {
            if (!selectedChoicesValue.contains(it)) {
                val deletedChoice = it.apply {
                    this.postId = "deleted"
                }
                FirebaseFirestore.getInstance()
                        .collection("choices")
                        .document(it.choiceId)
                        .set(deletedChoice)
            } else {
                selectedChoicesValue.remove(it)
            }
        }
        if (selectedChoicesValue.isNotEmpty()) {
            for (value in selectedChoicesValue) {
                value.postId = postId
                FirebaseFirestore.getInstance()
                        .collection("choices")
                        .document(value.choiceId)
                        .set(value)
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "CHOICES_FAILED", Toast.LENGTH_SHORT).show()
                        }
            }
        } else return
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

    fun selectTile(tile: Tile) {
        selectedTile.postValue(tile)
        tempChoice.tileType = tile
        Timber.d("selectedTile = ${tile.tileId}")
    }

    fun setWay(index: Int) {
        tempChoice.way = index
        setChoice()
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
        Timber.d("imageId = ${list.map { it.tileType.tileId }}")
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