package com.example.assignment4.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class FunFactRepository(val scope : CoroutineScope,
                        private val dao : FunFactDao
) {

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })

        }
    }
    val allFacts = dao.factList()
    fun getFact(){
        scope.launch {
            try {
                Log.e("REPO", "Fetching fun fact...")
                val fact: FunFact =
                    client.get("https://uselessfacts.jsph.pl//api/v2/facts/random").body()
                dao.addFunFact(fact)
            }
            catch (e: Exception)
            {
                Log.e("FunFact Activity", "Error fetching", e)
            }
            Log.e("REPO", "told the DAO")
        }
    }

}