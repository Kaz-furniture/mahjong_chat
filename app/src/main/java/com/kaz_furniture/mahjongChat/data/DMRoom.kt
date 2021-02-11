package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
class DMRoom: Serializable {
    var roomId  = "${System.currentTimeMillis()}"
    var createdAt = Date()
    var updatedAt = Date()
    var deletedAt: Date? = null
    var userIds = listOf<String>()

    companion object {
        fun getOpponentUserId(dmRoom: DMRoom): String {
            val usersList = ArrayList<String>().apply {
                this.addAll(dmRoom.userIds)
                this.remove(myUser.userId)
            }
            return usersList[0]
        }
    }
}