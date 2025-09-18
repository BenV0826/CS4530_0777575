package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MyViewModel : ViewModel() {
    private val _mutableCourses = MutableStateFlow(value = listOf<Course>())
    val coursesReadOnly: StateFlow<List<Course>> = _mutableCourses

    private val _courseFieldNumber = MutableStateFlow("")
    val courseNumberReadOnly: StateFlow<String> = _courseFieldNumber

    private val _courseFieldDept = MutableStateFlow("")
    val courseDeptReadOnly: StateFlow<String> = _courseFieldDept

    private val _courseFieldLocation = MutableStateFlow("")
    val courseLocationReadOnly: StateFlow<String> = _courseFieldLocation

    private val _expandedCourseNumber = MutableStateFlow<String?>(null)
    val expandedCourseNumberReadOnly: StateFlow<String?> = _expandedCourseNumber

    /**
     * Setter for number field
     */
    fun updateNumberField(number: String) {
        _courseFieldNumber.value = number
    }

    /**
     * Setter for department field
     */
    fun updateDeptField(department: String) {
        _courseFieldDept.value = department
    }

    /**
     * Setter for location field
     */
    fun updateLocationField(location: String) {
        _courseFieldLocation.value = location
    }

    /**
     * Set all fields
     * @params given course number, course department, and course location
     */
    fun updateAllFields(number: String, department: String, location: String) {
        updateNumberField(number)
        updateDeptField(department)
        updateLocationField(location)
    }

    /**
     * Add a course to the course list in viewmodel
     */
    fun addCourse(number: String, department: String, location: String, isExpanded: Boolean) {
        if (_mutableCourses.value.find({ it.courseNumber == number }) != null) return
        _mutableCourses.value += Course(number, department, location)
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
                        courseDepartment = newDepartment, courseLocation = newLocation
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
        _mutableCourses.value = _mutableCourses.value.filterNot({ it.courseNumber == number })
    }


    fun getCourseInfo(courseNumber: String): Course {
        val lookup = _mutableCourses.value.find({ it.courseNumber == courseNumber })
        if (lookup != null) {
            return lookup
        }
        return Course("", "", "")
    }

    /**
     * View model helper to change the state of the vm to indicate that the course associated with
     * the given string should be expanded.
     * @param courseNumber : The course number corresponding with the Card that should be expanded
     *
     */
    fun toggleCourseExpansion(courseNumber: String ) {

        val previousValue = _expandedCourseNumber.value // store previous (for debug)
        if (previousValue == courseNumber ) {
            _expandedCourseNumber.value = null // the same course is selected we minimize the card
            return
        }
        _expandedCourseNumber.value = courseNumber
    }

    var inEditMode by mutableStateOf(false)

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                val vm = MyViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(), bottomBar = {
                        MyBottomBar(vm, {}, {}, {})
                    }) { innerPadding ->
                    Column(modifier = Modifier.padding(30.dp)) {
                        CourseFields(vm)
                        CourseList(
                            vm, modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun CourseFields(vm: MyViewModel) {
        val numberInput by vm.courseNumberReadOnly.collectAsState()
        val deptInput by vm.courseDeptReadOnly.collectAsState()
        val locationInput by vm.courseLocationReadOnly.collectAsState()
        Row {
            Modifier.fillMaxWidth()
            TextField(
                value = numberInput, onValueChange = { newText ->
                    vm.updateNumberField(newText)
                }, label = { Text("Course Number") }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            TextField(
                value = deptInput, onValueChange = { newText ->
                    vm.updateDeptField(newText)
                }, label = { Text("Department") }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        TextField(
            value = locationInput, onValueChange = { newText ->
                vm.updateLocationField(newText)
            }, label = { Text("Course Location") }, modifier = Modifier.fillMaxWidth()
        )

    }

    // Ensure necessary imports are present at the top of your .kt file
// (e.g., androidx.compose.material3.*, androidx.compose.runtime.*, androidx.compose.ui.*, etc.)

    @Composable
    fun ClickableCourse(
        course: Course,
        isExpanded: Boolean,
        onItemClick: (Course) -> Unit,
        vm: MyViewModel
    ) {
        val elevation = animateDpAsState(if (isExpanded) 8.dp else 2.dp, label = "elevation_card")
        val courses by vm.coursesReadOnly.collectAsState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.value),
            onClick = {

                vm.toggleCourseExpansion(course.courseNumber)
                onItemClick(course)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${course.courseDepartment} ${course.courseNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!isExpanded && course.courseLocation.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Location: ${course.courseLocation}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, // Kept specific import path for TextOverflow for clarity
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            "Detailed Report:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        DetailItem(label = "Course Number", value = course.courseNumber)
                        DetailItem(label = "Department", value = course.courseDepartment)
                        DetailItem(label = "Full Location", value = course.courseLocation.ifBlank { "N/A" })



                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (vm.inEditMode) {
                                    vm.updateCourse(course.courseNumber, vm.courseDeptReadOnly.value,
                                        vm.courseLocationReadOnly.value)
                                }
                                else {
                                    //course.courseDepartment = vm.courseDeptReadOnly.value
                                    vm.updateAllFields(course.courseNumber, course.courseDepartment, course.courseLocation)

                                }
                                vm.inEditMode = !vm.inEditMode
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            if (vm.inEditMode) {
                                Text("Save")

                            } else {
                                Text("Edit Details")

                            }

                        }
                    }
                }
            }
        }
    }

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



    @Composable
    fun CourseList(vm: MyViewModel, modifier: Modifier = Modifier) {
        val courses by vm.coursesReadOnly.collectAsState()
        println("CourseList RECOMPOSING - expandedCourseNumber from VM: $vm.expandedCourseNumberReadOnly.value") // <<< ADD THIS LOG
        val context = LocalContext.current
        if (courses.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center


            ) {
                Text(text = "No courses to display")
            }
            return
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,


        ) {
            items(
                items = courses,
                key = { course -> course.courseNumber }
            ) { courseItem ->
                val shouldBeExpanded = courseItem.courseNumber == vm.expandedCourseNumberReadOnly.collectAsState().value

                println("CourseList ITEM - Course: ${courseItem.courseNumber}, shouldBeExpanded: $shouldBeExpanded (comparing with '${vm.expandedCourseNumberReadOnly}')") // <<< ADD THIS LOG
                ClickableCourse(
                    vm = vm,
                    course = courseItem,
                    isExpanded = shouldBeExpanded,
                    onItemClick = { clickedCourse ->

                        vm.updateAllFields(clickedCourse.courseNumber, clickedCourse.courseDepartment, clickedCourse.courseLocation)
                        vm.toggleCourseExpansion(clickedCourse.courseNumber)
                        println("Course ${clickedCourse.courseNumber} clicked. Should be expanded? $shouldBeExpanded")


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
        vm: MyViewModel, addCourse: () -> Unit, editCourse: () -> Unit, removeCourse: () -> Unit
    ) {
        // Referenced Android Kotlin documentation for bottom bar scaffold :
        // https://developer.android.com/develop/ui/compose/quick-guides/content/display-bottom-app-bar
        val numberInput by vm.courseNumberReadOnly.collectAsState()
        val deptInput by vm.courseDeptReadOnly.collectAsState()
        val locationInput by vm.courseLocationReadOnly.collectAsState()
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(), actions = {
                Row {
                    Modifier.fillMaxWidth()
                    Button(
                        onClick = {
                            vm.addCourse(
                                numberInput, deptInput, locationInput, false
                            )
                        }, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        Text(text = "Add New", fontSize = 18.sp)
                    }



                    Button(
                        onClick = { vm.removeCourse(vm.courseNumberReadOnly.value) },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .fillMaxSize()

                    ) {
                        Text(text = "Remove", textAlign = TextAlign.Center, fontSize = 18.sp)
                    }
                }
            })
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Assignment2Theme {}
    }
}