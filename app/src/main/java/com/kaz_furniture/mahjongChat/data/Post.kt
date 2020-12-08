package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Post {
    var postId = "${System.currentTimeMillis()}"
    var userId: String? = ""
    var userName: String = ""
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var updatedAt: Date? = null
    var explanation: String? = ""
    var imageUrl: String? = ""

}