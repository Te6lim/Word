package com.te6lim.word.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.te6lim.keyboard.KeyBoardView
import com.te6lim.word.R
import com.te6lim.word.databinding.FragmentGameBinding
import com.te6lim.wordgame.GameBoard
import com.te6lim.wordgame.WordGame

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        val wordGame = WordGame()
        val gameBoard = binding.gameBoard

        gameBoard.setUpWithBoard(wordGame)

        val keyBoard = binding.keyBoardView

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
        return binding.root
    }
}