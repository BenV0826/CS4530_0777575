package com.example.assignment4

import android.app.Application
import androidx.room.Room
import com.example.assignment4.data.FunFactDatabase
import com.example.assignment4.data.FunFactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FunFactApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())

    lateinit var db: FunFactDatabase
        private set

    lateinit var funFactRepository: FunFactRepository
        private set

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            FunFactDatabase::class.java,
            "fun_fact_database"
        ).build()

        funFactRepository = FunFactRepository(scope, db.funFactDao())
    }
}
