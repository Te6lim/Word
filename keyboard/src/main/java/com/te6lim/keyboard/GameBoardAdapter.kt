package com.te6lim.keyboard

import android.graphics.Color

interface GameBoardAdapter {

    enum class GuessState {
        MISPLACED, WRONG
    }

    val colorWrong: Int
        get() = Color.rgb(120, 124, 127)

    val colorMisplaced: Int
        get() = Color.rgb(201, 180, 87)

    fun paintKeys(letters: List<Char>, state: GuessState)

    fun getColorOfState(state: GuessState) = when (state) {
        GuessState.MISPLACED -> colorMisplaced
        else -> colorWrong
    }
}