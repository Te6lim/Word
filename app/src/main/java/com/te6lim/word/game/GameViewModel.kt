package com.te6lim.word.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.te6lim.wordgame.WordGame

class GameViewModel(game: WordGame) : ViewModel() {

    private val _gameInstance = MutableLiveData(game)

    val gameInstance: LiveData<WordGame>
        get() = _gameInstance

    class GameViewModelFactory(private val game: WordGame) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java))
                return GameViewModel(game) as T
            throw IllegalArgumentException("unknown view model class")
        }
    }
}