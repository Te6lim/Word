package com.te6lim.word.game

import com.te6lim.word.repository.WordRepository

class WordGame(wordRepository: WordRepository? = null) {

    companion object {
        private const val MAX_TRIAL = 6
    }

    private var t = 0

    private var word: String = "clown"

    var guesses: List<GuessInfo> = mutableListOf()
        private set

    init {
    }

    fun guessWord(guessWord: String): GuessInfo? {
        verifyWord(guessWord)
        if (t < MAX_TRIAL) {
            val guessInfo = GuessInfo(guessWord, word)
            guesses = guesses.toMutableList().apply { add(guessInfo.apply { trial = ++t }) }
            return guessInfo.copy()
        }
        return null
    }

    private fun verifyWord(w: String) {
        if (w.length < word.length || w.length > word.length) throw IllegalArgumentException()
    }

    class GuessInfo(private val guess: String, private val word: String) {
        var characterArray = arrayListOf<Char>()

        private var misplacedCharacters = listOf<Char>()

        private var wrongCharacters = listOf<Char>()

        var trial = 0

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
            for ((i, c) in word.withIndex()) {
                if (c != guess[i] && guess.contains(c)) characters.add(c)
            }
            return characters
        }

        private fun wrongCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            for (c in word) {
                if (!guess.contains(c)) characters.add(c)
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
            return GuessInfo(guess, word).also {
                it.misplacedCharacters = this.misplacedCharacters
                it.trial = this.trial
                it.wrongCharacters = this.wrongCharacters
            }
        }
    }
}