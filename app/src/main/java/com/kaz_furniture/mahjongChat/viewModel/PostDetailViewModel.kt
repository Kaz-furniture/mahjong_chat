package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.view.ChoicesCommentsView
import timber.log.Timber

class PostDetailViewModel: ViewModel() {
    val contentInput = MutableLiveData<String>()
    val items = MutableLiveData<List<ChoicesCommentsView.Adapter.ChoiceCommentData>>()
    val isSelectedLiveData = MutableLiveData<Boolean>()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(contentInput) { result.value = submitValidation()}
    }
    private val isSelected: Boolean
        get() = choices.any { it.userIds.contains(myUser.userId) }
    private var choices = listOf<Choice>()
        set(value) {
            field = value
            updateItems()
        }
    private var comments = listOf<Comment>()
        set(value) {
            field = value
            updateItems()
        }

    private fun submitValidation(): Boolean {
        val messageValue = contentInput.value
        return !messageValue.isNullOrBlank()
    }

    private fun updateItems() {
        val list = mutableListOf<ChoicesCommentsView.Adapter.ChoiceCommentData>()
        list.addAll(choices.map {
            ChoicesCommentsView.Adapter.ChoiceCommentData().apply {
                choice = it
            }
        })
        list.addAll(comments.map {
            ChoicesCommentsView.Adapter.ChoiceCommentData().apply {
                comment = it
            }
        })
        items.postValue(list)
        isSelectedLiveData.postValue(isSelected)
    }

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
            this.addAll(comments)
            this.add(newComment)
        }
        comments = newCommentsList
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
                    comments = result
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
                        choices = result
                        Toast.makeText(applicationContext, "Choices Get Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "CHOICES_FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

//    private fun choicesCheck(list: List<Choice>) {
//        for (value in list) {
//            if (value.userIds.contains(myUser.userId)) {
//                val sendList = listOf<Choice>(value)
//                isSelected = true
//                choicesList.postValue(sendList)
//                Timber.d("choicesCheck")
//                return
//            }
//        }
//        Timber.d("choicesCheckOK = ${list.size}")
//        isSelected = false
//        choicesList.postValue(list)
//    }

    fun choiceSelect(choice: Choice) {

        val newChoice = choice.apply {
            if (isSelected) {
                this.userIds.remove(myUser.userId)
            } else {
                this.userIds.add(myUser.userId)
            }
        }

        FirebaseFirestore.getInstance()
                .collection("choices")
                .document(choice.choiceId)
                .set(newChoice)
                .addOnCompleteListener {
                    choices = choices.map {
                        if (choice.choiceId == it.choiceId) {
                            newChoice
                        } else it
                    }
                    Toast.makeText(applicationContext, "CHOICE_SELECTED", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "CHOICE_FAILED", Toast.LENGTH_SHORT).show()
                }

    }
}