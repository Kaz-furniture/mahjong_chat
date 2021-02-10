package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class DMMessage {
    var messageId = "${System.currentTimeMillis()}"
    var createdAt = Date()
    var deletedAt: Date? = null
    var content = ""
    var fromUserId = ""
    var roomId = ""
}