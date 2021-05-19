package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Notification {
    var notificationId: String = "${System.currentTimeMillis()}"
    var fromUserId: String = ""
    var toUserId: String = ""
    var type: Int = 0
    var submitTime = Date()
    var content = ""
}