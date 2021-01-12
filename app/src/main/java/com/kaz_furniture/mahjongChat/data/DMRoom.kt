package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class DMRoom {
    var roomId  = "${System.currentTimeMillis()}"
    var createdAt = Date()
    var updatedAt = Date()
    var deletedAt: Date? = null
    var userIds = listOf<String>()

    companion object {
        fun getOpponentUserId(dmRoom: DMRoom, myUserId: String): String {
            val usersList = ArrayList<String>().apply {
                this.addAll(dmRoom.userIds)
                this.remove(myUserId)
            }
            return usersList[0]
        }
    }
}