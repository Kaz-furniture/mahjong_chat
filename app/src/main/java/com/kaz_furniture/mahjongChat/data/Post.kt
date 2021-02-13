package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Post: Serializable {
    var postId = "${System.currentTimeMillis()}"
    var userId: String = ""
    var userName: String = ""
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var updatedAt: Date = Date()
    var explanation: String? = ""
    var imageUrl: String? = ""
    var favoriteUserIds: List<String> = listOf()
}