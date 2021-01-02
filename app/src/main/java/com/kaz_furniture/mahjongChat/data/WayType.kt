package com.kaz_furniture.mahjongChat.data

import com.kaz_furniture.mahjongChat.R

enum class WayType(val wayId: Int, val textId: Int) {
    DISCARD(0, R.string.wayCut),
    DISCARD_CALL(1, R.string.wayCutCall),
    DISCARD_SILENT(2, R.string.waySilent),
    PON(3, R.string.pon),
    CHI(4, R.string.chi),
    KAN(5, R.string.kan),
    THROUGH(6, R.string.through);

    companion object {
        fun getWayTypeById(id:Int): WayType = values().firstOrNull{ it.wayId ==id } ?:DISCARD
    }
}