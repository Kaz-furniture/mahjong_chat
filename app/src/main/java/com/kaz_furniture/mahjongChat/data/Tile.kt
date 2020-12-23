package com.kaz_furniture.mahjongChat.data

import com.kaz_furniture.mahjongChat.R

enum class Tile(val tileId: Int, val imageId: Int) {
    M1(1, R.drawable.m1),
    M2(2, R.drawable.m2),
    M3(3, R.drawable.m3),
    M4(4, R.drawable.m4),
    M5(5, R.drawable.m5),
    M5R(6, R.drawable.m5r),
    M6(7, R.drawable.m6),
    M7(8, R.drawable.m7),
    M8(9, R.drawable.m8),
    M9(10, R.drawable.m9),
    P1(11, R.drawable.p1),
    P2(12, R.drawable.p2),
    P3(13, R.drawable.p3),
    P4(14, R.drawable.p4),
    P5(15, R.drawable.p5),
    P5R(16, R.drawable.p5r),
    P6(17, R.drawable.p6),
    P7(18, R.drawable.p7),
    P8(19, R.drawable.p8),
    P9(20, R.drawable.p9),
    S1(21, R.drawable.s1),
    S2(22, R.drawable.s2),
    S3(23, R.drawable.s3),
    S4(24, R.drawable.s4),
    S5(25, R.drawable.s5),
    S5R(26, R.drawable.s5r),
    S6(27, R.drawable.s6),
    S7(28, R.drawable.s7),
    S8(29, R.drawable.s8),
    S9(30, R.drawable.s9),
    Z1(31, R.drawable.z1),
    Z2(32, R.drawable.z2),
    Z3(33, R.drawable.z3),
    Z4(34, R.drawable.z4),
    Z5(35, R.drawable.z5),
    Z6(36, R.drawable.z6),
    Z7(37, R.drawable.z7);

    companion object {
        fun getTileById(id: Int): Tile {
            return values().firstOrNull { it.tileId == id} ?:M1
        }
    }
}