package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

open class BaseActivity: AppCompatActivity() {
    protected fun hideKeyboard(view: View) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun launchLoginActivity() {
        FirebaseAuth.getInstance().signOut()
        LoginActivity.start(this)
    }
}