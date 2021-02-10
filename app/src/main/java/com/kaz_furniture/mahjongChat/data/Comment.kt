package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Comment {
    var commentId: String = "${System.currentTimeMillis()}"
    var userId: String = ""
    var postId: String = ""
    var content: String = ""
    var createdAt = Date()
    var deletedAt: Date? = null
}