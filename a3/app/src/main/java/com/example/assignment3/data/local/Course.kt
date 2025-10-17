package com.example.assignment3.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Course(
    @PrimaryKey val courseNumber : String,
    @ColumnInfo( name = "course_department" ) var courseDepartment : String,
    @ColumnInfo ( name = "course_location" ) var courseLocation : String
)
