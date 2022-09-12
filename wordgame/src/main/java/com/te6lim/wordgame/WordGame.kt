package com.te6lim.wordgame

class WordGame(private val source: WordSource) {

    companion object {
        const val MAX_TRIAL = 6
        var WORD_LENGTH = 5
            private set
    }

    enum class CharState {
        IN_PLACE, OUT_OF_PLACE, WRONG
    }

    private var t = 0

    private var characterCount = mutableMapOf<Char, Int>()

    private var word: String = ""
        set(value) {
            field = value.uppercase()
            resetCharacterCount()
        }

    private var guessWord = StringBuilder()

    private var guesses: List<GuessInfo> = mutableListOf()

    init {
        word = source.getWord()
    }

    private fun resetCharacterCount() {
        characterCount = mutableMapOf()
        for (c in word) {
            if (characterCount.containsKey(c)) {
                characterCount.replace(c, characterCount[c]!! + 1)
            } else
                characterCount[c] = 1
        }
    }

    internal fun addLetter(letter: Char) {
        if (guessWord.length < WORD_LENGTH) guessWord.append(letter)
    }

    internal fun removeLastLetter() {
        if (guessWord.isNotEmpty()) guessWord.deleteCharAt(guessWord.lastIndex)
    }

    internal fun getAllGuesses(): List<GuessInfo> {
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

    internal fun removeAllCharacters() {
        guessWord = StringBuilder()
    }

    open inner class GuessInfo internal constructor(guess: String, t: Int) : GameBoard.WordState,
        WordGameHelper {

        internal var misplacedCharacters = listOf<Char>()

        internal var wrongCharacters = listOf<Char>()

        internal var correctCharacters = listOf<Char>()

        internal val trial = t

        internal val guessWord = guess.uppercase()

        private val states = arrayListOf<CharState>()

        init {

            resetCharacterCount()

            for (i in 0 until WORD_LENGTH) states.add(CharState.WRONG)

            if (guess.isNotEmpty()) {
                misplacedCharacters = misplacedCharacters()
                wrongCharacters = wrongCharacters()
                correctCharacters = correctCharacters()
            }
        }

        private fun correctCharacters(): List<Char> {
            resetCharacterCount()
            val characters = mutableListOf<Char>()
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            for ((i, c) in guessWord.withIndex()) {
                if (c == wordArray[i]) {
                    if (characterCount[c]!! > 0) {
                        characterCount.replace(c, characterCount[c]!! - 1)
                        states[i] = CharState.IN_PLACE
                    }
                    characters.add(c)
                    wordArray[i] = '\u0000'
                }
            }
            return characters
        }

        internal fun characterState(index: Int): CharState {
            /*if (word[index] == guessWord[index]) {
                if (characterCount[guessWord[index]]?: -1 > 0) {
                    characterCount[guessWord[index]] = characterCount[guessWord[index]]!! - 1
                    return CharState.IN_PLACE
                }
            }
            if (correctCharacters.contains(guessWord[index])) return CharState.WRONG
            if (misplacedCharacters.contains(guessWord[index])) {
                if (characterCount[guessWord[index]]?: -1 > 0) {
                    characterCount[guessWord[index]] = characterCount[guessWord[index]]!! - 1
                    return CharState.OUT_OF_PLACE
                }
                else CharState.WRONG
            }
            return CharState.WRONG*/
            return states[index]
        }

        private fun misplacedCharacters(): List<Char> {
            resetCharacterCount()
            val characters = mutableListOf<Char>()
            val wordArray = arrayListOf<Char>()
            for (c in word) wordArray.add(c)
            var flag = false
            for ((i, c) in guessWord.withIndex()) {

                for (j in 0 until WORD_LENGTH) {
                    if (j < guessWord.length && guessWord[j] == wordArray[j] && c == guessWord[j]) flag = true
                }

                if (!flag && c != wordArray[i] && wordArray.contains(c)) {
                    if (characterCount[c]!! > 0) {
                        characterCount.replace(c, characterCount[c]!! - 1)
                        states[i] = CharState.OUT_OF_PLACE
                    }
                    characters.add(c)
                    wordArray[wordArray.indexOf(c)] = '\u0000'
                }
                flag = false
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
            return guessWord == word
        }

        override fun getStateByPosition(p: Int, letter: Char): CharState {
            if (letter == word[p]) return CharState.IN_PLACE
            return if (word.contains(letter.uppercaseChar())) CharState.OUT_OF_PLACE
            else CharState.WRONG
        }

        override fun getCharacterCount(): Map<Char, Int> {
            val map = mutableMapOf<Char, Int>()
            for (c in misplacedCharacters) {
                if (map.containsKey(c)) map.replace(c, map[c]?.plus(1) ?: 0)
                else map[c] = 1
            }
            return map
        }
    }
}

interface WordGameHelper {
    fun getCharacterCount(): Map<Char, Int>
}