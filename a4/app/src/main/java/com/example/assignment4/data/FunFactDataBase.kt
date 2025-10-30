package com.example.assignment4.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities = [FunFact::class], version = 1, exportSchema = false)
abstract class FunFactDatabase : RoomDatabase() {
    abstract fun funFactDao(): FunFactDao
}

    @Dao
    interface FunFactDao {
        @Insert
        suspend fun addFunFact(fact: FunFact)

        @Query("SELECT * from funFact ORDER BY id DESC" )
        fun factList() : Flow<List<FunFact>>


    }



