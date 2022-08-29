package com.te6lim.keyboard

abstract class GameBoardAdapter {

    enum class GuessState {
        MISPLACED, WRONG
    }

    var colorWrong: Int? = null

    var colorMisplaced: Int? = null

    abstract fun paintKeys(letters: List<Char>, state: GuessState)

    fun highlightKeys(misplaced: List<Char>, wrong: List<Char>) {
        paintKeys(wrong, GuessState.WRONG)
        paintKeys(misplaced, GuessState.MISPLACED)
    }

    fun getColorOfState(state: GuessState) = when (state) {
        GuessState.MISPLACED -> colorMisplaced
        else -> colorWrong
    }
}