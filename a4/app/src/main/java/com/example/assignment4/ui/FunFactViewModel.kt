package com.example.assignment4.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assignment4.FunFactApplication
import com.example.assignment4.data.FunFact
import com.example.assignment4.data.FunFactRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

class FunFactViewModel (private val funFactRepository  : FunFactRepository): ViewModel() {

    val allFacts : StateFlow<List<FunFact>> = funFactRepository.allFacts.stateIn(
        scope = funFactRepository.scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()

    )


    suspend fun getFact (){
        try{
            funFactRepository.getFact()
        }
        catch (e: Exception)
        {
            Log.e("FunFact Activity", "Error fetching", e)
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
