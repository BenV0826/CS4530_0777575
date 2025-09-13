package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class MyViewModel : ViewModel() {
    private val _mutableCourses = MutableStateFlow(value = listOf<Course>())
    val coursesReadOnly: StateFlow<List<Course>> = _mutableCourses

    private val _courseNumber = MutableStateFlow("")
    val courseNumberReadOnly: StateFlow<String> = _courseNumber

    private val _courseDept = MutableStateFlow("")
    val courseDeptReadOnly: StateFlow<String> = _courseDept

    private val _courseLocation = MutableStateFlow("")
    val courseLocationReadOnly: StateFlow<String> = _courseLocation

    fun updateCourseNumber(number: String) {
        _courseNumber.value = number
    }

    fun updateCourseDept(department: String) {
        _courseDept.value = department
    }

    fun updateCourseLocation(location: String) {
        _courseLocation.value = location
    }

    fun addCourse(number: String, department: String, location: String) {
        if (_mutableCourses.value.find({ it.courseNumber == number }) != null) return
        _mutableCourses.value += Course(number, department, location)
    }

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
                vm.updateCourseNumber(newText)
            }, label = { Text("Course Number") }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            TextField(
                value = deptInput, onValueChange = { newText ->
                vm.updateCourseDept(newText)
            }, label = { Text("Department") }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        TextField(
            value = locationInput, onValueChange = { newText ->
            vm.updateCourseLocation(newText)
        }, label = { Text("Course Location") }, modifier = Modifier.fillMaxWidth()
        )

    }

    @Composable
    fun ClickableCourse(course: Course, onItemClick: (Course) -> Unit = {}) {
        Text(
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            text = ("Clickable"),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable(onClick = { onItemClick(course) })
                // Asked Gemini "Is there a way to surround a lazy column
                // with a border?"
                .border(
                    width = 1.dp, Color.Black, shape = MaterialTheme.shapes.medium
                )
        )
    }


    @Composable
    fun CourseList(vm: MyViewModel, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
        val courses by vm.coursesReadOnly.collectAsState()

        LazyColumn(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .clickable(onClick = { onClick() }),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            items(
                count = courses.size,
            ) { index ->
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    text = ("${courses.elementAt(index).courseDepartment} ${courses.elementAt(index).courseNumber} "),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        // Asked Gemini "Is there a way to surround a lazy column
                        // with a border?"
                        .border(
                            width = 1.dp, Color.Black, shape = MaterialTheme.shapes.medium
                        )
                )

                if (courses.elementAt(index).courseNumber == vm.courseNumberReadOnly.collectAsState().value) {
                    Modifier.border(
                        width = 1.dp, Color.Green, shape = MaterialTheme.shapes.medium
                    )
                }


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
                                numberInput, deptInput, locationInput
                            )
                        }, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        Text(text = "Add New", fontSize = 18.sp)
                    }

                    Button(
                        onClick = {
                            val courseEdit = vm.getCourseInfo(numberInput)
                            if (vm.inEditMode) {
                                vm.updateCourse(numberInput, deptInput, locationInput)
                                println("number: ${numberInput}, dept: ${deptInput}, location: ${locationInput}")
                            }
                            if (!vm.inEditMode) {
                                vm.updateCourse(numberInput, deptInput, locationInput)
                                vm.updateCourseDept(courseEdit.courseDepartment)
                                vm.updateCourseLocation(courseEdit.courseLocation)
                            }


                            vm.inEditMode = !vm.inEditMode
                        }, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .fillMaxSize()

                    ) {
                        if (vm.inEditMode) {
                            Text(text = "Save Changes", fontSize = 18.sp)
                        } else {
                            Text(text = "Edit", fontSize = 18.sp)
                        }
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