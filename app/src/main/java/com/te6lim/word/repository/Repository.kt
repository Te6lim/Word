package com.te6lim.word.repository

import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApi
import com.te6lim.word.toWordList
import com.te6lim.wordgame.WordSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Repository(private val wordDatabase: WordDatabase, private val network: WordApi) : WordSource() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private var currentWord: String = ""

    init {
        scope.launch {
            val remainingWords = wordDatabase.wordDao.getAll()
            if (remainingWords.isEmpty()) {
                try {
                    val words = getWordsAsync()
                    val wordList = words.toWordList().toMutableList()
                    currentWord = wordList.removeAt(0).data!!
                    wordDatabase.wordDao.insertWords(wordList)
                } catch (e: Exception) {
                    currentWord = ""
                }
            } else {
                val wordList = remainingWords.toMutableList()
                currentWord = wordList.removeAt(0).data!!
                wordDatabase.wordDao.insertWords(wordList)
            }
        }
    }

    override fun getWord(): String {
        return currentWord
    }

    private suspend fun getWordsAsync(): List<String> {
        return network.wordApiService.getWordAsync(network.apiKey).await()
    }

}