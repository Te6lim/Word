package com.te6lim.word

import android.app.Application
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApi
import com.te6lim.word.repository.Repository

class WordApplication : Application() {

    var darkThemeActive = false
        set(value) {
            field = value
            if (value) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    private lateinit var wordDB: WordDatabase
    private lateinit var network: WordApi

    lateinit var repository: Repository
        private set

    override fun onCreate() {
        super.onCreate()
        val apiKey = this.packageManager.getApplicationInfo(
            this.packageName, PackageManager.GET_META_DATA
        ).metaData.getString("WORD_KEY")

        wordDB = WordDatabase.getInstance(this)
        network = WordApi.getInstance(apiKey!!)

        repository = Repository(wordDB, network)
    }
}