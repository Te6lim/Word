package com.te6lim.wordgame

class WordGame(private val source: WordSource? = null) {

    companion object {
        const val MAX_TRIAL = 6
        var WORD_LENGTH = 5
            private set
    }

    private var t = 0

    private var word: String = "GLOVE"
        set(value) {
            field = value.uppercase()
        }

    private var guessWord = StringBuilder()

    private var guesses: List<GuessInfo> = mutableListOf()

    internal fun addLetter(letter: Char) {
        if (guessWord.length < WORD_LENGTH) guessWord.append(letter)
    }

    internal fun removeLastLetter() {
        if (guessWord.isNotEmpty()) guessWord.deleteCharAt(guessWord.lastIndex)
    }

    fun getAllGuesses(): List<GuessInfo> {
        if (t < MAX_TRIAL - 1 && guessWord.isNotEmpty()) {
            val guessInfo = GuessInfo(guessWord.toString(), t++)
            guesses = guesses.toMutableList().apply { add(guessInfo) }
            guessWord = StringBuilder()
        }
        return guesses
    }

    internal fun getLatestGuess(): GuessInfo? {
        if (t < MAX_TRIAL) {
            return if (guessWord.length < WORD_LENGTH) GuessInfo(guessWord.toString(), t)
            else {
                val guessInfo = GuessInfo(guessWord.toString(), t++)
                guesses = guesses.toMutableList().apply { add(guessInfo) }
                guessWord = StringBuilder()
                guessInfo
            }
        }
        return null
    }

    open inner class GuessInfo internal constructor(guess: String, t: Int) {

        internal var misplacedCharacters = listOf<Char>()

        internal var wrongCharacters = listOf<Char>()

        internal val trial = t

        internal val guessWord = guess.uppercase()

        internal var unUsedCharacters = arrayListOf<Char>()

        init {
            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters()
                wrongCharacters = wrongCharacters()
                resetUnselectedCharacters()
            }
        }

        fun resetUnselectedCharacters() {
            for (c in word) unUsedCharacters.add(c)
        }

        private fun misplacedCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            for ((i, c) in guessWord.withIndex()) {
                if (c != wordArray[i] && wordArray.contains(c)) {
                    characters.add(c)
                    wordArray[i] = '\u0000'
                }
            }
            return characters
        }

        private fun wrongCharacters(): List<Char> {
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            val characters = mutableListOf<Char>()
            for ((i, c) in guessWord.withIndex()) {
                if (!wordArray.contains(c)) {
                    characters.add(c)
                    wordArray[i] = '\u0000'
                }
            }
            return characters
        }

        internal fun isMisplaced(char: Char): Boolean {
            if (misplacedCharacters.contains(char.uppercaseChar())) return true
            return false
        }

        internal fun isWrong(char: Char): Boolean {
            if (wrongCharacters.contains(char.uppercaseChar())) return true
            return false
        }

        internal fun isRight(char: Char): Boolean {
            return !this.isMisplaced(char.uppercaseChar()) && !this.isWrong(char.uppercaseChar())
        }

        internal fun isCorrect(): Boolean {
            return guessWord.isNotEmpty() && misplacedCharacters.isEmpty() && wrongCharacters.isEmpty()
        }
    }
}