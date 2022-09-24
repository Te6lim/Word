package com.te6lim.word.repository

import com.te6lim.word.database.Word
import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApi
import com.te6lim.word.toWordList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Repository(private val wordDatabase: WordDatabase, private val network: WordApi) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _currentWord = MutableStateFlow<Word?>(null)
    val currentWord = _currentWord

    init {
        scope.launch {
            val remainingWords = wordDatabase.wordDao.getAll()
            if (remainingWords.isEmpty()) {
                try {
                    val words = getWordsAsync()
                    val wordList = words.toWordList().toMutableList()
                    _currentWord.value = wordList.removeAt(0)
                    wordDatabase.wordDao.insertWords(wordList)
                } catch (e: Exception) {
                    _currentWord.value = null
                }
            } else {
                val wordList = remainingWords.toMutableList()
                _currentWord.value = wordList.removeAt(0)
                wordDatabase.wordDao.insertWords(wordList)
            }
        }
    }

    private suspend fun getWordsAsync(): List<String> {
        return network.wordApiService.getWordAsync(network.apiKey).await()
    }

}