package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class DMRoom {
    var roomId  = "${System.currentTimeMillis()}"
    var createdAt = Date()
    var deletedAt: Date? = null
    var opponentUserId = ""
    var ownerUserId = ""
}