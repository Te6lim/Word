package com.te6lim.word.repository

import com.te6lim.word.database.Word
import com.te6lim.word.database.WordDatabase
import com.te6lim.word.network.WordApi
import com.te6lim.word.toWordList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

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
                    withContext(Dispatchers.IO) {
                        wordDatabase.wordDao.insertWords(wordList).apply {
                            _currentWord.value = wordDatabase.wordDao.getWord(this[0])
                        }
                    }
                } catch (e: Exception) {
                    _currentWord.value = null
                }
            } else {
                val wordList = remainingWords.toMutableList()
                _currentWord.value = wordList[0]
            }
        }
    }

    private suspend fun getWordsAsync(): List<String> {
        return network.wordApiService.getWordAsync(network.apiKey).await()
    }

    fun markWordAsUsed(id: Long) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val word = wordDatabase.wordDao.getWord(id).apply { isUsed = true }
                wordDatabase.wordDao.insert(word)
            }
        }
    }

}