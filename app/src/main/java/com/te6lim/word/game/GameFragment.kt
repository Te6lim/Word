package com.te6lim.word.game

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.te6lim.keyboard.KeyBoardView
import com.te6lim.word.MainActivity
import com.te6lim.word.R
import com.te6lim.word.WordApplication
import com.te6lim.word.databinding.FragmentGameBinding
import com.te6lim.word.settings.SettingsBottomSheet
import com.te6lim.wordgame.GameBoard
import com.te6lim.wordgame.WordGame

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private lateinit var menuProvider: MenuProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)

        (requireActivity() as MainActivity).supportActionBar?.title = null

        menuProvider = getMenu()

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    private fun getMenu() = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.game_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.profile -> {
                    true
                }
                R.id.settings_screen -> {
                    val settingsBottomSheet = SettingsBottomSheet().apply {
                        setItemListener(object : SettingsBottomSheet.SettingsItemListener {
                            override fun onThemeSelected(isDarkTheme: Boolean) {
                                (requireActivity().application as WordApplication)
                                    .darkThemeActive = isDarkTheme
                            }
                        })
                    }
                    settingsBottomSheet.show(
                        requireActivity().supportFragmentManager, SettingsBottomSheet.TAG
                    )
                    true
                }
                R.id.help -> {
                    findNavController().navigate(GameFragmentDirections.actionGameFragmentToHelpFragment())
                    true
                }
                else -> return false
            }
        }

    }
}