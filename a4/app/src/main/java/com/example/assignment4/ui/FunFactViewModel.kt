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


    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })

        }
    }

    val allFacts : StateFlow<List<FunFact>> = funFactRepository.allFacts.stateIn(
        scope = funFactRepository.scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()

    )


    suspend fun getFact (){
        try{
            val responseText: FunFact = client.get("https://uselessfacts.jsph.pl//api/v2/facts/random.json?language=en").body()
            funFactRepository.addFact(responseText)
        }
        catch (e: Exception)
        {
            Log.e("FunFact Activity", "Error fetching", e)
        }
    }

}

// ... (rest of your file is correct)

object FunFactViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            // 1. Get the application instance from the initializer's context.
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FunFactApplication)

            // 2. Instantiate FunFactViewModel without the incorrect generic type.
            FunFactViewModel(
                funFactRepository = application.funFactRepository
            )
        }
    }
}
