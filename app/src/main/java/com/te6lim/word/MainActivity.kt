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

        val gameBoard = findViewById<GameBoard>(R.id.gameBoard)

        val game = WordGame()

        val keyBoard = findViewById<KeyBoardView>(R.id.gameKeyboard)

        keyBoard.setOnKeyClickListener(object : KeyBoardView.OnKeyClickListener {

            override fun onClick(char: Char) {
                game.addLetter(char)
                gameBoard.showCharacter(char)
            }

            override fun onClick(key: KeyBoardView.SpecialKeys) {
                when (key) {
                    KeyBoardView.SpecialKeys.ENTER -> {
                        gameBoard.guesses = game.getAllGuesses()
                    }

                    KeyBoardView.SpecialKeys.DELETE -> {
                        game.removeLastLetter()
                        gameBoard.clearLastCharacter()
                    }
                }
            }
        })

    }
}