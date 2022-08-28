package com.te6lim.keyboard

import android.graphics.Color

interface GameBoardAdapter {

    enum class GuessState {
        CORRECT, MISPLACED, WRONG
    }

    val colorCorrect: Int
        get() = Color.rgb(107, 170, 100)

    val colorWrong: Int
        get() = Color.rgb(120, 124, 127)

    val colorMisplaced: Int
        get() = Color.rgb(201, 180, 87)

    fun paintKeys(letters: List<Char>, state: GuessState)

    fun getColorOfState(state: GameBoardAdapter.GuessState) = when (state) {
        GameBoardAdapter.GuessState.CORRECT -> colorCorrect
        GameBoardAdapter.GuessState.MISPLACED -> colorMisplaced
        else -> colorWrong
    }
}