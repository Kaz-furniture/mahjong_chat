package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import timber.log.Timber

class PostDetailViewModel: ViewModel() {
    val contentInput = MutableLiveData<String>()
    val choicesList = MutableLiveData<List<Choice>>()
    val commentsList = MutableLiveData<List<Comment>>()
    private val saveList = ArrayList<Choice>()
    private var isSelected: Boolean? = null

    fun submitComment(postId: String) {
        val newComment = Comment().apply {
            this.content = contentInput.value ?:return
            this.postId = postId
            this.userId = myUser.userId
        }
        FirebaseFirestore.getInstance()
                .collection("comment")
                .document(newComment.commentId)
                .set(newComment)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "COMMENT_SUCCESS", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "COMMENT_FAILED", Toast.LENGTH_SHORT).show()
                }
        val newCommentsList = ArrayList<Comment>().apply {
            this.add(newComment)
            this.addAll(commentsList.value ?: listOf())
        }
        commentsList.postValue(newCommentsList)
    }

    fun getComments(postId: String) {
        FirebaseFirestore.getInstance()
                .collection("comment")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener {task ->
                    val result = task.result?.toObjects(Comment::class.java) ?: kotlin.run {
                        Toast.makeText(applicationContext, "NO_COMMENT", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    commentsList.postValue(result)
                }
    }

    fun getChoices(postId: String) {
        FirebaseFirestore.getInstance()
                .collection("choices")
                .whereEqualTo("postId", postId)
                .get()
                .addOnCompleteListener { task ->
                    val result = task.result?.toObjects(Choice::class.java) ?: kotlin.run {
                        Toast.makeText(applicationContext, "Choices Failed", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                    if (task.isSuccessful) {
                        saveList.addAll(result)
                        choicesCheck(result)
                        Toast.makeText(applicationContext, "Choices Get Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "CHOICES_FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun choicesCheck(list: List<Choice>) {
        for (value in list) {
            if (value.userIds.contains(myUser.userId)) {
                val sendList = listOf<Choice>(value)
                isSelected = true
                choicesList.postValue(sendList)
                Timber.d("choicesCheck")
                return
            }
        }
        Timber.d("choicesCheckOK = ${list.size}")
        isSelected = false
        choicesList.postValue(list)
    }

    fun choiceSelect(choice: Choice) {
        if (isSelected == false) {
            val newChoice = choice.apply {
                this.userIds.add(myUser.userId)
            }
            FirebaseFirestore.getInstance()
                    .collection("choices")
                    .document(choice.choiceId)
                    .set(newChoice)
                    .addOnCompleteListener {
                        isSelected = true
                        val sendList = listOf<Choice>(newChoice)
                        choicesList.postValue(sendList)
                        Toast.makeText(applicationContext, "CHOICE_SELECTED", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "CHOICE_FAILED", Toast.LENGTH_SHORT).show()
                    }
        } else {
            val newChoice = choice.apply {
                this.userIds.remove(myUser.userId)
            }
            FirebaseFirestore.getInstance()
                    .collection("choices")
                    .document(choice.choiceId)
                    .set(newChoice)
                    .addOnCompleteListener {
                        isSelected = false
                        choicesList.postValue(saveList)
                        Toast.makeText(applicationContext, "CHOICE_UPDATED", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "CHOICE_FAILED", Toast.LENGTH_SHORT).show()
                    }
        }
    }
}