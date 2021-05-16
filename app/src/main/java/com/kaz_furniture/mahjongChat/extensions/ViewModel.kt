package com.kaz_furniture.mahjongChat.extensions

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.FcmRequest
import com.kaz_furniture.mahjongChat.data.User
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

fun ViewModel.sendFcm(sendToUser: User, type: Int, key1: String, key2: String, onFailure: () -> Unit = {}, onComplete: () -> Unit = {}) {
    Timber.d("sendToUser = ${sendToUser.name}")
    if (sendToUser.userId == myUser.userId) return
    Timber.d("sendToUser = ${sendToUser.name}")
    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    val fcmRequestBody = FcmRequest().apply {
        to = sendToUser.fcmToken
        data.apply {
            this.type = type
            this.key1 = key1
            this.key2 = key2
        }
    }
    val json = Gson().toJson(fcmRequestBody)
    val request = Request.Builder()
        .url("https://fcm.googleapis.com/fcm/send")
        .addHeader("Authorization", "key=${R.string.fcmTokenServerKey}")
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(MediaType.parse("application/json"), json))
        .build()
    Timber.d("json:$json")
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Timber.d("onFailure e:${e.message}")
            onFailure.invoke()
        }
        override fun onResponse(call: Call, response: Response) {
            Timber.d("onResponse")
            response.body()?.string()?.also {
                Timber.d("response:$it")
            }
            onComplete.invoke()
        }
    })
}