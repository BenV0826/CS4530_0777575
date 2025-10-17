package com.example.assignment3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("SELECT * from course ORDER BY course_department ASC")
    fun getAllCourses(): Flow<List<Course>>
}
