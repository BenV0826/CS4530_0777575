package com.example.assignment4.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment4.FunFactApplication
import com.example.assignment4.data.FunFact
import com.example.assignment4.data.FunFactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FunFactViewModel (private val funFactRepository  : FunFactRepository): ViewModel() {

    val allFacts : StateFlow<List<FunFact>> = funFactRepository.allFacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()

    )


    fun getFact (){
        viewModelScope.launch {
            funFactRepository.getFact()
        }
    }

    fun clearFacts(){
        viewModelScope.launch {
            funFactRepository.clearFacts()
        }
    }

}

object FunFactViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FunFactApplication)
            FunFactViewModel(
                funFactRepository = application.funFactRepository
            )
        }
    }
}
