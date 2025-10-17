package com.example.assignment3

import com.example.assignment3.data.local.Course
import com.example.assignment3.data.local.CourseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(private val scope: CoroutineScope, private val courseDao: CourseDao) {

    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    fun addCourse(course: Course) {
        scope.launch {
            courseDao.insert(course)
        }
    }

    fun deleteCourse(course: Course) {
        scope.launch {
            courseDao.delete(course)
        }
    }
}
