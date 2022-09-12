package com.te6lim.word.repository

import androidx.lifecycle.Transformations
import com.te6lim.word.database.WordDatabase
import com.te6lim.word.models.Response
import com.te6lim.word.network.WordApiService
import com.te6lim.wordgame.WordSource

class Repository(wordDatabase: WordDatabase, private val network: WordApiService) : WordSource() {

    private val remainingWords = wordDatabase.wordDao.getAll()


    private val isWordAvailable = Transformations.map(remainingWords) {
        it?.isNotEmpty() ?: false
    }

    override fun getWord(): String {
        return remainingWords.value?.get(0)!!.data!!
    }

    private suspend fun getWordsAsync(): Response {
        return network.getWord().await()
    }

}