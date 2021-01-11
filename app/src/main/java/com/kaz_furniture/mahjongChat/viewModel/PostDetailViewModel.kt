package com.kaz_furniture.mahjongChat.viewModel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.applicationContext
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.data.Choice
import timber.log.Timber

class PostDetailViewModel: ViewModel() {

    val choicesList = MutableLiveData<List<Choice>>()
    private val saveList = ArrayList<Choice>()
    private var isSelected: Boolean? = null

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