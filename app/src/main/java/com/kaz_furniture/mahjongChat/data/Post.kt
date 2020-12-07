package com.kaz_furniture.mahjongChat.data

import java.util.*

class Post {
    var postId = "${System.currentTimeMillis()}"
    var userId: String? = ""
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var updatedAt: Date? = null
    var explanation: String? = ""
    var imageUrl: String? = ""

}