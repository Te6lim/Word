package com.te6lim.word.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.te6lim.word.repository.Repository
import com.te6lim.wordgame.WordGame
import kotlinx.coroutines.flow.map

class GameViewModel(game: WordGame, private val repository: Repository) : ViewModel() {

    private val _gameInstance = MutableLiveData(game)

    val word = repository.currentWord.map {
        it?.data
    }

    val gameInstance: LiveData<WordGame>
        get() = _gameInstance

    fun markCurrentWordAsUsed() {
        repository.currentWord.value?.let {
            repository.markWordAsUsed(it.id)
        }
    }

    class GameViewModelFactory(
        private val game: WordGame, private val repository: Repository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java))
                return GameViewModel(game, repository) as T
            throw IllegalArgumentException("unknown view model class")
        }
    }
}