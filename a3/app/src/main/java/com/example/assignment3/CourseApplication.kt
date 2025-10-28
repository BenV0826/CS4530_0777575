package com.example.assignment3
import android.app.Application
import com.example.assignment3.data.local.CourseDatabase

class CourseApplication : Application() {
    private val database by lazy { CourseDatabase.getDatabase(this) }
    val repository by lazy { Repository(database.courseDao()) }
}
