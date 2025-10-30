package com.example.assignment4.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity
data class FunFact(
    @PrimaryKey
    val id : String,
    var text : String,
    var source_url : String?=null)
