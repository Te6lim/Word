package com.te6lim.wordgame

class WordGame(private val source: WordSource? = null) {

    companion object {
        const val MAX_TRIAL = 6
        var WORD_LENGTH = 5
            private set
    }

    enum class CharState {
        IN_PLACE, OUT_OF_PLACE, WRONG
    }

    private var t = 0

    private var word: String = "CLAMP"
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

    internal fun generateStats() {

    }

    open inner class GuessInfo internal constructor(guess: String, t: Int) : GameBoard.WordState {

        internal var misplacedCharacters = listOf<Char>()

        internal var wrongCharacters = listOf<Char>()

        internal var correctCharacters = listOf<Char>()

        internal val trial = t

        internal val guessWord = guess.uppercase()

        internal var unUsedCharacters = arrayListOf<Char>()

        init {
            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters()
                wrongCharacters = wrongCharacters()
                correctCharacters = correctCharacters()
                resetUnselectedCharacters()
            }
        }

        fun resetUnselectedCharacters() {
            for (c in word) unUsedCharacters.add(c)
        }

        private fun correctCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            for ((i, c) in guessWord.withIndex()) {
                if (c == wordArray[i]) {
                    characters.add(c)
                    wordArray[i] = '\u0000'
                }
            }
            return characters
        }

        internal fun characterState(index: Int): CharState {
            if (word[index] == guessWord[index] && correctCharacters.contains(guessWord[index]))
                return CharState.IN_PLACE
            if (correctCharacters.contains(guessWord[index])) return CharState.WRONG
            if (misplacedCharacters.contains(guessWord[index])) return CharState.OUT_OF_PLACE
            return CharState.WRONG
        }

        private fun misplacedCharacters(): List<Char> {
            val characters = mutableListOf<Char>()
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            for ((i, c) in guessWord.withIndex()) {
                if (c != wordArray[i] && wordArray.contains(c)) {
                    characters.add(c)
                    wordArray[wordArray.indexOf(c)] = '\u0000'
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

        internal fun isCorrect(): Boolean {
            return guessWord.isNotEmpty() && misplacedCharacters.isEmpty() && wrongCharacters.isEmpty()
        }

        override fun getStateByPosition(p: Int, letter: Char): CharState {
            if (letter == word[p]) return CharState.IN_PLACE
            return if (word.contains(letter.uppercaseChar())) CharState.OUT_OF_PLACE
            else CharState.WRONG
        }
    }
}