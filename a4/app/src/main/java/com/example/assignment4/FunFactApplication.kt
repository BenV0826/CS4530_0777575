package com.example.assignment4

import android.app.Application
import androidx.room.Room
import com.example.assignment4.data.FunFactDatabase
import com.example.assignment4.data.FunFactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.lazy


class FunFactApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())


    val db : FunFactDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FunFactDatabase::class.java,
            "fun_fact_database"
        ).build()
    }
    val funFactRepository by lazy { FunFactRepository(scope, db.funFactDao()) }
}

