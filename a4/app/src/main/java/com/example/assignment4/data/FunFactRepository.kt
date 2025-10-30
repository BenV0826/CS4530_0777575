package com.example.assignment4.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class FunFactRepository(val scope : CoroutineScope,
                        private val dao : FunFactDatabase.FunFactDao) {
    val allFacts = dao.factList()
    fun getFact(){
        scope.launch {
            Log.e("REPO", "Fetching fun fact...")

            val fetchedFact = FunFact(1,"test", "test")

            //now that we got the weather from our "slow network request" add it to the DB
            dao.addFunFact(fetchedFact)
            Log.e("REPO", "told the DAO")
        }
    }

}