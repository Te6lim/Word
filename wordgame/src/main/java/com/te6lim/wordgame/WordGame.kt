package com.te6lim.wordgame

class WordGame(private val repository: WordRepository? = null) {

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
            guesses = guesses.toMutableList().apply { add(guessInfo) }
            guessWord = StringBuilder()
        }
        return guesses
    }

    open inner class GuessInfo(guess: String) {

        private var misplacedCharacters = listOf<Char>()

        private var wrongCharacters = listOf<Char>()

        internal val guessWord = guess.uppercase()

        internal var unUsedCharacters = arrayListOf<Char>()

        init {
            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters()
                wrongCharacters = wrongCharacters()

                for (c in word) unUsedCharacters.add(c)
            }
        }

        fun resetUnUsedCharacters() {
            unUsedCharacters = arrayListOf()
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
    }
}