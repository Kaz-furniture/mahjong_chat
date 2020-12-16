package com.kaz_furniture.mahjongChat

import android.app.Application
import android.content.Context
import timber.log.Timber

class MahjongChatApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MahjongChatApplication.applicationContext = applicationContext
        initialize()
    }

    private fun initialize() {
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var applicationContext: Context

    }
}