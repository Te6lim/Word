package com.te6lim.word.repository

import com.te6lim.word.database.WordDatabase
import com.te6lim.word.models.Response
import com.te6lim.word.network.WordApiService
import com.te6lim.wordgame.WordSource

class Repository(wordDatabase: WordDatabase, private val network: WordApiService) : WordSource() {

    override fun getWord(): String {
        return ""
    }

    private suspend fun getWordsAsync(): Response {
        return network.getWord().await()
    }
}