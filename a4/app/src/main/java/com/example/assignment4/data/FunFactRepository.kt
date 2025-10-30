package com.example.assignment4.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FunFactRepository(val scope : CoroutineScope,
                        private val dao : FunFactDao
) {
    val allFacts = dao.factList()
    fun addFact(fact : FunFact){
        scope.launch {
            Log.e("REPO", "Fetching fun fact...")
            dao.addFunFact(fact)
            Log.e("REPO", "told the DAO")
        }
    }

}