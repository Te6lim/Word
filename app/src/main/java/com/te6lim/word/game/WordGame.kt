package com.te6lim.word.game

import com.te6lim.word.repository.WordRepository

class WordGame(wordRepository: WordRepository? = null) {

    companion object {
        private const val MAX_TRIAL = 6
        var WORD_LENGTH = 5
            private set
    }

    private var t = 0

    private var word: String = "SHIRE"
        set(value) {
            field = value.uppercase()
        }

    private var guessWord = StringBuilder()

    private var guesses: List<GuessInfo> = mutableListOf()

    fun addLetter(letter: Char) {
        if (guessWord.length < WORD_LENGTH) guessWord.append(letter)
    }

    fun removeLastLetter() {
        if (guessWord.isNotEmpty()) guessWord.deleteCharAt(guessWord.lastIndex)
    }

    fun getAllGuesses(): List<GuessInfo> {
        if (t < MAX_TRIAL && guessWord.isNotEmpty()) {
            val guessInfo = GuessInfo(guessWord.toString())
            guesses = guesses.toMutableList().apply { add(guessInfo.apply { trial = ++t }) }
            guessWord = StringBuilder()
        }
        return guesses
    }

    inner class GuessInfo(guess: String) {
        var characterArray = arrayListOf<Char>()

        private var misplacedCharacters = listOf<Char>()

        private var wrongCharacters = listOf<Char>()

        var trial = 0

        private val guessWord = guess.uppercase()

        init {
            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters()
                wrongCharacters = wrongCharacters()

                characterArray = arrayListOf()
                for (c in guess) characterArray.add(c)
            }
        }

        private fun misplacedCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            for ((i, c) in guessWord.withIndex()) {
                if (c != word[i] && word.contains(c)) {
                    characters.add(c)
                }
            }
            return characters
        }

        private fun wrongCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            for ((i, c) in guessWord.withIndex()) {
                if (!word.contains(c)) characters.add(c)
            }
            return characters
        }

        fun isCorrect() = misplacedCharacters.isEmpty() && wrongCharacters.isEmpty() && trial > 0

        fun isMisplaced(char: Char): Boolean {
            if (misplacedCharacters.contains(char.uppercaseChar())) return true
            return false
        }

        fun isWrong(char: Char): Boolean {
            if (wrongCharacters.contains(char.uppercaseChar())) return true
            return false
        }

        fun isRight(char: Char): Boolean {
            return !this.isMisplaced(char.uppercaseChar()) && !this.isWrong(char.uppercaseChar()) && trial > 0
        }

        fun copy(): GuessInfo {
            return GuessInfo(guessWord).also {
                it.misplacedCharacters = this.misplacedCharacters
                it.trial = this.trial
                it.wrongCharacters = this.wrongCharacters
            }
        }
    }
}