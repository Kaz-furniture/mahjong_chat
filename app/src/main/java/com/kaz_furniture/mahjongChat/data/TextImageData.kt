package com.kaz_furniture.mahjongChat.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.kaz_furniture.mahjongChat.BR
import com.kaz_furniture.mahjongChat.R

class TextImageData: BaseObservable() {

    @get:Bindable
    var text: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.text)
        }

    @get:Bindable
    var imageId: Int = R.drawable.dummy
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageId)
        }
}