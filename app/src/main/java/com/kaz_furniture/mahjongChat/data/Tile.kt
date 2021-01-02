package com.kaz_furniture.mahjongChat.data

import com.kaz_furniture.mahjongChat.R

enum class Tile(val tileId: Int, val imageId: Int, val hasTile: Boolean, val sectionName: String? = null) {
    M1(1, R.drawable.m1, true),
    M2(2, R.drawable.m2, true),
    M3(3, R.drawable.m3, true),
    M4(4, R.drawable.m4, true),
    M5(5, R.drawable.m5, true),
    M5R(6, R.drawable.m5r, true),
    M6(7, R.drawable.m6, true),
    M7(8, R.drawable.m7, true),
    M8(9, R.drawable.m8, true),
    M9(10, R.drawable.m9, true),
    P1(11, R.drawable.p1, true),
    P2(12, R.drawable.p2, true),
    P3(13, R.drawable.p3, true),
    P4(14, R.drawable.p4, true),
    P5(15, R.drawable.p5, true),
    P5R(16, R.drawable.p5r, true),
    P6(17, R.drawable.p6, true),
    P7(18, R.drawable.p7, true),
    P8(19, R.drawable.p8, true),
    P9(20, R.drawable.p9, true),
    S1(21, R.drawable.s1, true),
    S2(22, R.drawable.s2, true),
    S3(23, R.drawable.s3, true),
    S4(24, R.drawable.s4, true),
    S5(25, R.drawable.s5, true),
    S5R(26, R.drawable.s5r, true),
    S6(27, R.drawable.s6, true),
    S7(28, R.drawable.s7, true),
    S8(29, R.drawable.s8, true),
    S9(30, R.drawable.s9, true),
    Z1(31, R.drawable.z1, true),
    Z2(32, R.drawable.z2, true),
    Z3(33, R.drawable.z3, true),
    Z4(34, R.drawable.z4, true),
    Z5(35, R.drawable.z5, true),
    Z6(36, R.drawable.z6, true),
    Z7(37, R.drawable.z7, true),
    MSection(38, R.drawable.dummy, false, "萬子"),
    PSection(39, R.drawable.dummy, false, "筒子"),
    SSection(40, R.drawable.dummy, false, "索子"),
    ZSection(41, R.drawable.dummy, false, "字牌");

//    fun hasTile(tile: Tile): Boolean {
//        return tile.tileId <= 37
//    }

    companion object {
        fun getTileById(id: Int): Tile {
            return values().firstOrNull { it.tileId == id } ?:M1
        }
    }
}