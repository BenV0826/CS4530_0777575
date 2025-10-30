package com.example.assignment4.ui

import android.util.Log
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

class ViewMode (private val repository : FunFactRepository) {








    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })

        }
    }
    private val _factList = MutableStateFlow(listOf<String>())
    val factList : StateFlow<List<String>> = _factList

    val allFacts : StateFlow<List<FunFact>> = repository.allFacts.stateIn(
        scope = repository.scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()

    )


    suspend fun addFact (item: String){
        try{
            val responseText: FunFact = client.get("https://uselessfacts.jsph.pl//api/v2/facts/random.json?language=en").body()
            _factList.value = _factList.value + responseText.text
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
            FunFactViewModel(
                (this[AndroidviewModelFactory.APPLICATION_KEY] as FunFact])
            )
        }
    }
}