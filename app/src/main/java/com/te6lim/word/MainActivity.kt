package com.te6lim.word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.te6lim.word.game.GameBoard
import com.te6lim.word.game.WordGame

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gameBoard = findViewById<GameBoard>(R.id.gameBoard)

        val game = WordGame()

        //gameBoard.guesses = game.getAllGuesses()
    }
}