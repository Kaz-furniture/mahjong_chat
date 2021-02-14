package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.allPostList
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Comment
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.view.ChoicesCommentsView
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class PostDetailViewModel: ViewModel() {
    val contentInput = MutableLiveData<String>()
    val items = MutableLiveData<List<ChoicesCommentsView.Adapter.ChoiceCommentData>>()
    val isSelectedLiveData = MutableLiveData<Boolean>()
    val updatedPost = MutableLiveData<Post>()
    val starNumber = MutableLiveData<String>()
    val canSubmit = MediatorLiveData<Boolean>().also { result ->
        result.addSource(contentInput) { result.value = submitValidation()}
    }
    private val isSelected: Boolean
        get() = choices.any { it.userIds.contains(myUser.userId) }
    var choices = listOf<Choice>()
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

    fun starClick(post: Post) {
        val newUsers = ArrayList<String>().apply {
            this.addAll(post.favoriteUserIds)
        }
        if (post.favoriteUserIds.contains(myUser.userId)) {
            newUsers.remove(myUser.userId)
        } else {
            newUsers.add(myUser.userId)
        }
        val newPost = post.apply {
            this.favoriteUserIds = newUsers
        }

        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(newPost.postId)
                .set(newPost)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "STAR!", Toast.LENGTH_SHORT).show()
                    allPostList.apply {
                        this.remove(post)
                        this.add(newPost)
                        this.sortBy { value -> value.createdAt }
                    }
                    starNumber.postValue(newPost.favoriteUserIds.size.toString())
                }

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

    fun getPost(postId: String) {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val newPost = it.result?.toObject(Post::class.java)
                        updatedPost.postValue(newPost)
                    }
                }
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

    fun deletePost(post: Post) {
        val newPost = post.apply {
            deletedAt = Date()
        }
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(newPost.postId)
                .set(newPost)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(applicationContext, "DELETE_SUCCESS", Toast.LENGTH_SHORT).show()
                        allPostList.apply {
                            this.remove(post)
                            this.add(newPost)
                            this.sortBy { value -> value.createdAt }
                        }
                    }
                }
    }
}