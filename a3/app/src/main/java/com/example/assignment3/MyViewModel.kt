package com.example.assignment3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment3.data.local.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MyViewModel(private val repository: Repository) : ViewModel() {

    val coursesReadOnly: StateFlow<List<Course>> = repository.allCourses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _courseFieldNumber = MutableStateFlow("")
    val courseNumberReadOnly: StateFlow<String> = _courseFieldNumber

    private val _courseFieldDept = MutableStateFlow("")
    val courseDeptReadOnly: StateFlow<String> = _courseFieldDept

    private val _courseFieldLocation = MutableStateFlow("")
    val courseLocationReadOnly: StateFlow<String> = _courseFieldLocation

    private val _expandedCourseNumber = MutableStateFlow<String?>(null)
    val expandedCourseNumberReadOnly: StateFlow<String?> = _expandedCourseNumber
    var inEditMode by mutableStateOf(false)
    val INVALIDCN = ""

    fun updateNumberField(number: String) {
        _courseFieldNumber.value = number
    }

    fun updateDeptField(department: String) {
        _courseFieldDept.value = department
    }

    fun updateLocationField(location: String) {
        _courseFieldLocation.value = location
    }

    fun updateAllFields(number: String, department: String, location: String) {
        updateNumberField(number)
        updateDeptField(department)
        updateLocationField(location)
    }

    fun clearFields(){
        updateNumberField("")
        updateDeptField("")
        updateLocationField("")
    }

    fun upsertCourse(number: String, department: String, location: String) {
        val course = Course(number, department, location)
        repository.addCourse(course)
    }

    fun removeCourse(course: Course) {
        repository.deleteCourse(course)
    }

    fun setEditMode(editMode: Boolean) {
        inEditMode = editMode
    }

    fun setExpandedCourse(courseNumber: String?) {
        _expandedCourseNumber.value = courseNumber
    }
}
