package com.kaz_furniture.mahjongChat.data

class FcmRequest {
    var to: String = ""
    var data = Data()

    class Data {
        var type = 0
        var key1 = ""
        var key2 = ""
    }
}