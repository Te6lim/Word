package com.te6lim.word

import com.te6lim.word.game.WordGame

fun WordGame.GuessInfo.copy(): WordGame.GuessInfo {
    return WordGame.GuessInfo().also {
        it.misplacedCharacters = this.misplacedCharacters
        it.numberOfGuesses = this.numberOfGuesses
        it.wrongCharacters = this.wrongCharacters
        it.trials = this.trials
    }
}