package com.kaz_furniture.mahjongChat

import java.util.*

class User {
    var userId = ""
    var name = ""
    var createdAt: Date = Date()
    var deletedAt: Date? = null
    var introduction: String = ""
    var imageUrl: String = ""
    var followingUserId: List<String>? = null
}