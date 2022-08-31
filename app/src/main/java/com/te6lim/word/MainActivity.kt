package com.te6lim.word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.te6lim.keyboard.KeyBoardView
import com.te6lim.wordgame.GameBoard
import com.te6lim.wordgame.WordGame

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordGame = WordGame()
        val gameBoard = findViewById<GameBoard>(R.id.gameBoard)

        gameBoard.setUpWithBoard(wordGame)

        val keyBoard = findViewById<KeyBoardView>(R.id.gameKeyboard)

        with(keyBoard.gameBoardAdapter) {
            colorCorrect = gameBoard.correctColor
            colorMisplaced = gameBoard.misplacedColor
            colorWrong = gameBoard.wrongColor
        }

        gameBoard.setOnGuessSubmittedListener(object : GameBoard.SubmitListener {

            override fun onGuessSubmitted(correct: List<Char>, misplaced: List<Char>, wrong: List<Char>) {
                keyBoard.gameBoardAdapter.highlightKeys(correct, misplaced, wrong)
            }

        })

        keyBoard.setOnKeyClickListener(object : KeyBoardView.OnKeyClickListener {

            override fun onClick(char: Char) {
                gameBoard.setCharacter(char)
            }

            override fun onClick(key: KeyBoardView.SpecialKeys) {
                when (key) {
                    KeyBoardView.SpecialKeys.ENTER -> {
                        gameBoard.submitLatestGuess()
                    }

                    KeyBoardView.SpecialKeys.DELETE -> {
                        gameBoard.clearLastCharacter()
                    }
                }
            }
        })
    }
}