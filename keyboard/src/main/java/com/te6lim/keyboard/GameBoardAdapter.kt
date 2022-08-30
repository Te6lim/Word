package com.te6lim.keyboard

abstract class GameBoardAdapter {

    enum class GuessState {
        CORRECT, MISPLACED, WRONG
    }

    var colorCorrect: Int? = null

    var colorWrong: Int? = null

    var colorMisplaced: Int? = null

    abstract fun paintKeys(letters: List<Char>, state: GuessState)

    fun highlightKeys(correct: List<Char>, misplaced: List<Char>, wrong: List<Char>) {
        paintKeys(wrong, GuessState.WRONG)
        paintKeys(misplaced, GuessState.MISPLACED)
        paintKeys(correct, GuessState.CORRECT)
    }

    fun getColorOfState(state: GuessState) = when (state) {
        GuessState.MISPLACED -> colorMisplaced
        GuessState.CORRECT -> colorCorrect
        else -> colorWrong
    }
}