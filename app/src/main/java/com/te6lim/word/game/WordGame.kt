package com.te6lim.word.game

import com.te6lim.word.repository.WordRepository

class WordGame(wordRepository: WordRepository) {

    companion object {
        private const val MAX_TRIAL = 6
    }

    private val guessInfo = GuessInfo()

    private lateinit var word: String

    var guesses: List<GuessInfo> = mutableListOf()
        private set

    init {
    }

    fun guessWord(guessWord: String): GuessInfo {
        verifyWord(guessWord)
        if (guessInfo.trial <= MAX_TRIAL) {
            guessInfo.generateGetInfo(guessWord)
            guesses = guesses.toMutableList().apply { add(guessInfo) }
        }
        return guessInfo.copy()
    }

    private fun verifyWord(w: String) {
        if (w.length < word.length || w.length > word.length) throw IllegalArgumentException()
    }

    inner class GuessInfo {
        var trial = 0
            private set

        var characterArray = arrayListOf<Char>()

        private var misplacedCharacters = listOf<Char>()

        private var wrongCharacters = listOf<Char>()

        fun generateGetInfo(guess: String) {
            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters(guess)
                wrongCharacters = wrongCharacters(guess)
                ++trial

                characterArray = arrayListOf()
                for (c in guess) characterArray.add(c)
            }
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

        fun isCorrect() = misplacedCharacters.isEmpty() && wrongCharacters.isEmpty() && trial > 0

        fun isMisplaced(char: Char): Boolean {
            if (misplacedCharacters.contains(char)) return true
            return false
        }

        fun isWrong(char: Char): Boolean {
            if (wrongCharacters.contains(char)) return true
            return false
        }

        fun isRight(char: Char): Boolean {
            return !this.isMisplaced(char) && !this.isWrong(char)
        }

        fun copy(): WordGame.GuessInfo {
            return GuessInfo().also {
                it.misplacedCharacters = this.misplacedCharacters
                it.trial = this.trial
                it.wrongCharacters = this.wrongCharacters
            }
        }
    }
}