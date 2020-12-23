package com.kaz_furniture.mahjongChat.data

import com.kaz_furniture.mahjongChat.R

enum class WayType(val wayId: Int, val textId: Int) {
    DISCARD(1, R.string.wayCut),
    DISCARD_CALL(2, R.string.wayCutCall),
    DISCARD_SILENT(3, R.string.waySilent),
    PON(4, R.string.pon),
    CHI(5, R.string.chi),
    KAN(6, R.string.kan),
    THROUGH(7, R.string.through);

    companion object {
        fun getWayTypeById(id:Int): WayType = values().firstOrNull{ it.wayId ==id } ?:DISCARD
    }
}