package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaz_furniture.mahjongChat.viewModel.CreateAccountViewModel
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.User
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity
import com.kaz_furniture.mahjongChat.databinding.ActivityCreateAccountBinding
import java.util.*

class CreateAccountActivity: BaseActivity() {

    lateinit var binding: ActivityCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindingData: ActivityCreateAccountBinding? = DataBindingUtil.setContentView(
                this,
                R.layout.activity_create_account
        )
        binding = bindingData ?:return
        binding.lifecycleOwner = this
        binding.email = viewModel.email
        binding.password = viewModel.password
        binding.passwordConfirm = viewModel.passwordValidate
        binding.name = viewModel.name
        binding.canSubmit = viewModel.canSubmit
        binding.saveButton.setOnClickListener{
            createAuthUser()
        }
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun createAuthUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(viewModel.email.value ?:return, viewModel.password.value ?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseAuth.getInstance().currentUser?.uid?.also {
                            createUser(it)
                            return@addOnCompleteListener
                        }

                    } else {
                        Toast.makeText(this@CreateAccountActivity, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun createUser(uid: String) {
        val user = User().apply {
            userId = uid
            name = viewModel.name.value ?:""
            createdAt = Date()
//            deletedAt = null
//            introduction = ""
//            imageUrl = ""
//            followingUserId = null
        }
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        Toast.makeText(this@CreateAccountActivity, "Success", Toast.LENGTH_SHORT).show()
                        MainActivity.start(this)
                    } else {
                        Toast.makeText(this@CreateAccountActivity, "FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
    }


    companion object {
        fun start(activity: Activity) =
                activity.apply {
                    finishAffinity()
                    startActivity(Intent(activity, CreateAccountActivity::class.java))
                }
    }
}