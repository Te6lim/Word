package com.te6lim.word.repository

import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApiService
import com.te6lim.wordgame.WordSource

class Repository(wordDatabase: WordDatabase, network: WordApiService) : WordSource() {

    override fun getWord(): String {
        return ""
    }
}