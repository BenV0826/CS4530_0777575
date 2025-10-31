package com.example.assignment4.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class FunFactRepository(private val dao : FunFactDao, private val client : HttpClient) {
    val allFacts = dao.factList()
    suspend fun getFact() {
        try {
            Log.e("REPO", "Fetching fun fact...")
            val fact: FunFact =
                client.get("https://uselessfacts.jsph.pl/random.json?language=en").body()
            dao.addFunFact(fact)
            Log.e("REPO", "told the DAO")
        } catch (e: Exception) {
            Log.e("FunFact Activity", "Error fetching", e)
        }
    }
    suspend fun clearFacts(){
        dao.clearFacts()

    }
}