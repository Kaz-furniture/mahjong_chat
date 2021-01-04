package com.kaz_furniture.mahjongChat.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Choice {
    var choiceId: String = ""
    var postId: String = ""
    var number: Int = 0
    var userIds: List<String> = listOf()
    var tile: Int = 0
    var way: Int = 0

    @Exclude
    var wayType: WayType = WayType.DISCARD
        get() = WayType.getWayTypeById(way)

    @Exclude
    var tileType: Tile = Tile.M1
        get() = Tile.getTileById(tile)
        set(value) {
            field = value
            tile = value.tileId
        }
}