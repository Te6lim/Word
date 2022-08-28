package com.te6lim.word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.te6lim.keyboard.GameBoardAdapter
import com.te6lim.keyboard.KeyBoardView
import com.te6lim.wordgame.GameBoard
import com.te6lim.wordgame.WordGame

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordGame = WordGame()
        val gameBoard = findViewById<GameBoard>(R.id.gameBoard)
        gameBoard.setUpWithWordGame(wordGame)

        val keyBoard = findViewById<KeyBoardView>(R.id.gameKeyboard)

        gameBoard.setOnGuessSubmittedListener(object : GameBoard.SubmitListener {
            override fun onSubmit(
                correctChar: List<Char>, misplacedChars: List<Char>, wrongChar: List<Char>
            ) {
                with(keyBoard.gameBoardAdapter) {
                    paintKeys(correctChar, GameBoardAdapter.GuessState.CORRECT)
                    paintKeys(misplacedChars, GameBoardAdapter.GuessState.MISPLACED)
                    paintKeys(wrongChar, GameBoardAdapter.GuessState.WRONG)
                }
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