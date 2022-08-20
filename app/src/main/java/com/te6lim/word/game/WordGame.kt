package com.te6lim.word.game

import com.te6lim.word.copy
import com.te6lim.word.repository.WordRepository

class WordGame(wordRepository: WordRepository) {

    companion object {
        private const val MAX_TRIAL = 6
    }

    private val guessInfo = GuessInfo()

    private lateinit var word: String

    init {
    }

    fun guessWord(guessWord: String): GuessInfo {
        verifyWord(guessWord)
        if (guessInfo.numberOfGuesses <= MAX_TRIAL) {
            guessInfo.numberOfGuesses++
            guessInfo.misplacedCharacters = misplacedCharacters(guessWord)
            guessInfo.wrongCharacters = wrongCharacters(guessWord)
        }
        return guessInfo.copy()
    }

    private fun misplacedCharacters(wordString: String): List<Char> {
        val characters = mutableListOf<Char>()
        for ((i, c) in word.withIndex()) {
            if (c != wordString[i] && wordString.contains(c)) characters.add(c)
        }
        return characters
    }

    private fun wrongCharacters(wordString: String): List<Char> {
        val characters = mutableListOf<Char>()
        for (c in wordString) {
            if (!word.contains(c)) characters.add(c)
        }
        return characters
    }

    private fun verifyWord(w: String) {
        if (w.length < word.length || w.length > word.length) throw IllegalArgumentException()
    }

    class GuessInfo {
        var numberOfGuesses = 0

        var misplacedCharacters = listOf<Char>()

        var wrongCharacters = listOf<Char>()

        fun isCorrect() = misplacedCharacters.isEmpty() && wrongCharacters.isEmpty() && numberOfGuesses > 0
    }
}