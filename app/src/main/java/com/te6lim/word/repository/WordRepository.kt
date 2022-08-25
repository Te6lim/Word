package com.te6lim.word.repository

import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApiService
import com.te6lim.wordgame.WordRepository

class WordRepository(wordDatabase: WordDatabase, network: WordApiService) : WordRepository() {

    override fun getWord(): String {
        return ""
    }
}