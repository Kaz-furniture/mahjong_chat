package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class Choice: Serializable {
    var choiceId: String = "${System.currentTimeMillis()}"
    var postId: String = ""
    var number: Int = 0
    var userIds = ArrayList<String>()
    var tile: Int = 0
    var way: Int = 0

    @Exclude
    var wayType: WayType = WayType.DISCARD
        get() = WayType.getWayTypeById(way)
        set(value) {
            field = value
            way = value.wayId
        }

    @Exclude
    var tileType: Tile = Tile.M1
        get() = Tile.getTileById(tile)
        set(value) {
            field = value
            tile = value.tileId
        }
}