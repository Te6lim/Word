package com.te6lim.word.game

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.te6lim.keyboard.KeyBoardView
import com.te6lim.word.R
import com.te6lim.word.databinding.FragmentGameBinding
import com.te6lim.word.settings.SettingsBottomSheet
import com.te6lim.wordgame.GameBoard
import com.te6lim.wordgame.WordGame

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.game_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.profile -> {
                        true
                    }
                    R.id.settings_screen -> {
                        val settingsBottomSheet = SettingsBottomSheet()
                        settingsBottomSheet.show(
                            requireActivity().supportFragmentManager, SettingsBottomSheet.TAG
                        )
                        true
                    }
                    R.id.help -> {
                        true
                    }
                    else -> return false
                }
            }

        })

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