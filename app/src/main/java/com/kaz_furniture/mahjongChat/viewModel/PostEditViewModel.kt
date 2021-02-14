package com.kaz_furniture.mahjongChat.viewModel


import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.data.Tile
import timber.log.Timber
import java.util.*

class PostEditViewModel: ViewModel() {
    val selectedTile = MutableLiveData<Tile>()
    var tempChoice = Choice()
    var selectedChoices = MutableLiveData<List<Choice>>()
    val explanationInput = MutableLiveData<String>()

    fun postUpdate(post: Post) {
        val newPost = post.apply {
            updatedAt = Date()
            if (!explanationInput.value.isNullOrBlank()) {
                explanation = explanationInput.value
            }
        }
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.postId)
                .set(newPost)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "POST_UPDATED", Toast.LENGTH_SHORT).show()
                }
    }

    fun choicesUpdate(postId: String, addList: List<Choice>) {
        if (addList.isNotEmpty()) {
            for ((index, value) in addList.withIndex()) {
                value.postId = postId
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