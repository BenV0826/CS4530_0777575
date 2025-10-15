package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * The data class for a course.
 *
 * Makes up the "Model" part of my MVVM architecture
 */
data class Course(
    var courseNumber: String,
    var courseDepartment: String,
    var courseLocation: String
)// @TODO update code to use stateflow of FieldCourse instead of separate vals

/**
 * View Model class for course management App.
 */
class MyViewModel : ViewModel() {
    // Internal mutable list of courses the user is currently enrolled in
    private val _mutableCourses = MutableStateFlow(value = listOf<Course>())

    // Immutable course for recomposition of view
    val coursesReadOnly: StateFlow<List<Course>> = _mutableCourses

    // Use an instance of Course to be updated when fields are changed to store
    // their respective values.
    private val _courseFields = MutableStateFlow(value = Course("","", ""))
    val courseFieldsReadOnly: StateFlow<Course> = _courseFields

    // State flow to keep track of the course number whose card should be expanded
    private val _expandedCourseNumber = MutableStateFlow<String?>(null)
    val expandedCourseNumberReadOnly: StateFlow<String?> = _expandedCourseNumber

    // Track whether or not the user is editing a course
    var inEditMode by mutableStateOf(false)

    // Not allowing blank courses to be added
    val invalidClassNumber = ""

    /**
     * Setter to be called in the number field's OnChange to update courseFields'
     * number value.
     *
     * Make a copy to trigger recomposition
     */
    fun updateNumberField(number: String) {
        _courseFields.update { currentCourseFields ->
            currentCourseFields.copy(courseNumber = number)
        }
    }

    /**
     * Setter to be called in the department field's OnChange to update courseFields'
     * department value.
     *
     * Make a copy to trigger recomposition
     */
    fun updateDeptField(department: String) {
        _courseFields.update { currentCourseFields ->
            currentCourseFields.copy(courseDepartment = department)
        }
    }

    /**
     * Setter to be called on number field change to update course fields.
     *
     * Make a copy to trigger recomposition
     */
    fun updateLocationField(location: String) {
        _courseFields.update { currentCourseFields ->
            currentCourseFields.copy(courseLocation = location)
        }
    }

    /**
     * Given all parameters for Course, update the course fields as we make a copy
     * to trigger recomposition to avoid 3x recomposition, since this is an atomic operation.
     *
     */
    fun updateAllFields(number: String, department: String, location: String) {
        _courseFields.update { currentCourseFields ->
            currentCourseFields.copy(
                courseNumber = number,
                courseDepartment = department,
                courseLocation = location)
        }
    }

    /**
     * Similarly to updateAllFields, reassign values as we trigger recomposition
     */
    fun clearFields() {
        _courseFields.update { currentCourseFields ->
            currentCourseFields.copy(
                courseNumber = "",
                courseDepartment = "",
                courseLocation = "")
        }
    }


    /**
     * Try to add a course to the course list in ViewModel.
     * If the course already exists in the list, update the course.
     */
    fun addCourse(number: String, department: String, location: String) {
        if (_mutableCourses.value.find { it.courseNumber == number } == null) {
            _mutableCourses.value += Course(number, department, location)
            return
        }
        updateCourse(number, department, location)
    }

    /**
     * Given course department and location, update the given course number in the course list
     *
     * Creates a copy of the course list with the modified course details to trigger recomposition.
     */
    fun updateCourse(number: String, newDepartment: String, newLocation: String) {
        _mutableCourses.update { currentCourseList ->
            currentCourseList.map { existingCourse ->
                if (existingCourse.courseNumber == number) {
                    existingCourse.copy(
                        courseNumber = number,
                        courseDepartment = newDepartment,
                        courseLocation = newLocation
                    )

                } else {
                    existingCourse
                }
            }
        }
    }

    /**
     * Remove a course from the course list
     */
    fun removeCourse(number: String) {
        _mutableCourses.value = _mutableCourses.value.filterNot{ it.courseNumber == number }
    }

    /**
     * helper to explicitly set edit mode for code cleanliness
     */
    fun setEditMode(editMode: Boolean) {
        inEditMode = editMode
    }

    /**
     * Set the expanded course number in the ViewModel.
     *
     * If the course number to be expanded is invalid, remove it.
     */
    fun setExpandedCourse(courseNumber: String?) {
        if (_expandedCourseNumber.value == invalidClassNumber) removeCourse(invalidClassNumber)
        _expandedCourseNumber.value = courseNumber
    }
}

/**
 * Class for the singular activity in the course management app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                val vm = MyViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(), bottomBar = {
                        MyBottomBar(vm)
                    }) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        CourseList(
                            vm, modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxHeight()

                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ClickableCourse(
        course: Course, onItemClick: (Course) -> Unit, vm: MyViewModel
    ) {
        // collect state of expanded course number to determine if the card should be expanded
        val isExpanded =
            course.courseNumber == vm.expandedCourseNumberReadOnly.collectAsState().value

        // collect state of courseFieldsReadOnly
        val courseFields by vm.courseFieldsReadOnly.collectAsState()

        // animate the elevation of the card
        val elevation = animateDpAsState(
            if (isExpanded) 8.dp else 2.dp, label = "elevation_card"
        )
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 8.dp) // make height more appropriate

            ,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.value),
            onClick = {
                // If user clicks on an already expanded card, close it and save
                if (vm.expandedCourseNumberReadOnly.value == course.courseNumber) {
                    vm.setExpandedCourse(null)
                    vm.setEditMode(false)
                    // This return statement was auto filled by copilot and causes the card to
                    // collapse, which is the behavior I wanted to achieve, so I kept it.
                    return@Card
                }

                vm.setExpandedCourse(course.courseNumber)
                onItemClick(course)
            }) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = if (!isExpanded) Alignment.CenterVertically else Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Display the Course department and number for unexpanded card
                        if (course.courseNumber != vm.invalidClassNumber) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "${course.courseDepartment} ${course.courseNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 40.sp,
                                )
                        }
                    }
                }
                // AnimatedVisibility to show textFields if editing and course details if not for the
                // expanded card
                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        if (vm.inEditMode) {
                            val headerText =
                                if (course.courseNumber == vm.invalidClassNumber) "Add Course" else "Edit Course Details"
                            Text(
                                headerText,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                value = courseFields.courseNumber,
                                onValueChange = { newNumber ->
                                    vm.updateNumberField(newNumber)
                                },
                                label = { Text("Course Number") })
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                value = courseFields.courseDepartment,
                                onValueChange = { newDept ->
                                    vm.updateDeptField(newDept)
                                },
                                label = { Text("Course Department") })
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                value = courseFields.courseLocation,
                                onValueChange = { newLocation ->
                                    vm.updateLocationField(newLocation)
                                },
                                label = { Text("Course Location") })

                        } else {
                            Text(
                                "Detailed Report:",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            DetailItem(label = "Course Number", value = course.courseNumber)
                            DetailItem(label = "Department", value = course.courseDepartment)
                            DetailItem(
                                label = "Location", value = course.courseLocation.ifBlank { "N/A" })


                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    // Remove the old course with the given course number so that
                                    // when the user edits the course number a "new" course
                                    // is not added to the course list in addition to keeping
                                    // the old course number. (implementation decision)
                                    vm.removeCourse(course.courseNumber)
                                    vm.addCourse(
                                        courseFields.courseNumber,
                                        courseFields.courseDepartment,
                                        courseFields.courseLocation
                                    )
                                    // close card when saved (implementation decision)
                                    if (vm.inEditMode) {
                                        vm.setExpandedCourse(null)
                                    }
                                    vm.setEditMode(!vm.inEditMode)

                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)

                            ) {
                                if (vm.inEditMode) {
                                    Text("Save")
                                } else {
                                    Text("Edit Details")
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.removeCourse(course.courseNumber) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Text("Remove")
                            }


                        }
                    }
                }
            }
        }
    }

    /**
     * Helper for displaying and formatting values displayed in the expanded view of a card
     */
    @Composable
    private fun DetailItem(label: String, value: String) {
        Row(modifier = Modifier.padding(vertical = 2.dp)) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    /**
     * Composable containing the LazyColumn
     */
    @Composable
    fun CourseList(vm: MyViewModel, modifier: Modifier = Modifier) {
        val courses by vm.coursesReadOnly.collectAsState()
        if (courses.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No courses to display")
            }
            vm.setEditMode(false)
            return
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            items(
                items = courses, key = { course -> course.courseNumber }) { courseItem ->
                ClickableCourse(
                    vm = vm, course = courseItem, onItemClick = { clickedCourse ->
                        vm.setExpandedCourse(clickedCourse.courseNumber)
                        vm.updateAllFields(
                            clickedCourse.courseNumber,
                            clickedCourse.courseDepartment,
                            clickedCourse.courseLocation
                        )
                    })
            }
        }
    }

    /**
     * Composable function for creating the bottom bar containing
     * buttons for adding, editing, and removing courses.
     *
     */

    @Composable
    fun MyBottomBar(
        vm: MyViewModel
    ) {
        // Referenced Android Kotlin documentation for bottom bar scaffold :
        // https://developer.android.com/develop/ui/compose/quick-guides/content/display-bottom-app-bar
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(), actions = {
                Row {
                    Modifier.fillMaxWidth()
                    Button(
                        onClick = {
                            vm.clearFields()
                            // Add an invalid class to the course list so that it is composed in
                            // the lazy column so the user can start editing it.
                            vm.addCourse(
                                vm.invalidClassNumber, "", ""
                            )
                            vm.setEditMode(true)
                            vm.setExpandedCourse(vm.invalidClassNumber)
                        }, enabled = !vm.inEditMode, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                    ) {
                        Text(text = "Add New", fontSize = 18.sp)
                    }
                }
            })
    }
}