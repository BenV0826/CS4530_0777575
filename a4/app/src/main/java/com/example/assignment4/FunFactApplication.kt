package com.example.assignment4

import android.app.Application
import androidx.room.Room
import com.example.assignment4.data.FunFactDatabase
import com.example.assignment4.data.FunFactRepository
import com.example.assignment4.network.KtorClient

class FunFactApplication : Application() {
    val database: FunFactDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FunFactDatabase::class.java,
            "fun_fact_database"
        ).build()
    }

    val funFactRepository: FunFactRepository by lazy {
        FunFactRepository(
            dao = database.funFactDao(),
            client = KtorClient.httpClient
        )
    }
}
