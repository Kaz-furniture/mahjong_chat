package com.kaz_furniture.mahjongChat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val updateData = MutableLiveData<Boolean>()
}