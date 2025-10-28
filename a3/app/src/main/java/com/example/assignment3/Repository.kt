package com.example.assignment3

import com.example.assignment3.data.local.Course
import com.example.assignment3.data.local.CourseDao
import kotlinx.coroutines.flow.Flow

class Repository( private val courseDao: CourseDao) {

    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun addCourse(course: Course) {
            courseDao.insert(course)

    }

    suspend fun deleteCourse(course: Course) {
            courseDao.delete(course)
    }
}
