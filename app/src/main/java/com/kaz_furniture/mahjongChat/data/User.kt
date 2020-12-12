package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class User {
    var userId = ""
    var name = ""
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var introduction: String = ""
    var imageUrl: String = ""
    var followingUserId: List<String> = listOf()
}