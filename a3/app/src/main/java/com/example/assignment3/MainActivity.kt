package com.example.assignment3

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment3.ui.theme.Assignment3Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Entity
data class Course(
    @PrimaryKey val courseNumber : String,
    @ColumnInfo( name = "course_department" ) var courseDepartment : String,
    @ColumnInfo ( name = "course_location" ) var courseLocation : String

)

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
    var inEditMode by mutableStateOf(false)
    val INVALIDCN = "-1"
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

    fun clearFields(){
        updateNumberField("")
        updateDeptField("")
        updateLocationField("")
    }


    /**
     * Add a course to the course list in viewmodel
     */
    fun addCourse(number: String, department: String, location: String) {
        removeCourse(INVALIDCN)

        if (_mutableCourses.value.find { it.courseNumber == number } == null) {
            _mutableCourses.value  += Course(number, department, location)
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
                        courseNumber = number, courseDepartment = newDepartment, courseLocation = newLocation
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

    fun setEditMode(editMode: Boolean) {
        inEditMode = editMode
    }

    fun setExpandedCourse(courseNumber: String?) {
        if(_expandedCourseNumber.value == INVALIDCN)
            removeCourse(INVALIDCN)
        _expandedCourseNumber.value = courseNumber
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment3Theme {
                val vm = MyViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(), bottomBar = {
                        MyBottomBar(vm, {}, {}, {})
                    }) { innerPadding ->
                    Column(modifier = Modifier.padding(30.dp)) {
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

    // Ensure necessary imports are present at the top of your .kt file
    // (e.g., androidx.compose.material3.*, androidx.compose.runtime.*, androidx.compose.ui.*, etc.)

    @Composable
    fun ClickableCourse(
        course: Course,
        onItemClick: (Course) -> Unit,
        vm: MyViewModel
    ) {
        val isExpanded = course.courseNumber == vm.expandedCourseNumberReadOnly.collectAsState().value

        val courseNumberForTextField by vm.courseNumberReadOnly.collectAsState() // Correct way to get the value for TextField
        val courseDepartmentForTextField by vm.courseDeptReadOnly.collectAsState()
        val courseLocationForTextField by vm.courseLocationReadOnly.collectAsState()

        val elevation = animateDpAsState(if (isExpanded)
            8.dp else 2.dp, label = "elevation_card")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp) // make height more appropriate
                .height(500.dp)
            ,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.value),
            onClick = {
                if(vm.inEditMode) vm.setEditMode(false)
                if(vm.expandedCourseNumberReadOnly.value == course.courseNumber) {
                    vm.setExpandedCourse(null)

                    // This return statement was autofilled by copilot and causes the card to
                    // collapse, which is the behavior I wanted to achieve, so I kept it.
                    return@Card
                }

                vm.setExpandedCourse(course.courseNumber)
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
                            text = if(course.courseNumber == vm.INVALIDCN) "" else "${course.courseDepartment} ${course.courseNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!isExpanded && course.courseLocation.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Location: ${course.courseLocation}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis, // Kept specific import path for TextOverflow for clarity
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        if (vm.inEditMode) {
                            val headerText = if (course.courseNumber == vm.INVALIDCN) "Add Course" else "Edit Course Details"
                            Text(
                                headerText,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                value = courseNumberForTextField,
                                onValueChange = { newNumber ->
                                    vm.updateNumberField(newNumber)
                                },
                                label = { Text("Course Number") }
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                value = courseDepartmentForTextField,
                                onValueChange = { newDept ->
                                    vm.updateDeptField(newDept)
                                },
                                label = { Text("Course Department") }
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                value = courseLocationForTextField,
                                onValueChange = { newLocation ->
                                    vm.updateLocationField(newLocation)
                                },
                                label = { Text("Course Location") }
                            )

                        } else {
                            Text(
                                "Detailed Report:",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            DetailItem(label = "Course Number", value = course.courseNumber)
                            DetailItem(label = "Department", value = course.courseDepartment)
                            DetailItem(
                                label = "Full Location",
                                value = course.courseLocation.ifBlank { "N/A" })


                        }
                        Spacer(Modifier.height(8.dp))
                        Row{
                            Button(
                                onClick = {
                                    vm.addCourse(
                                        courseNumberForTextField,
                                        courseDepartmentForTextField,
                                        courseLocationForTextField
                                    )
                                    // handle when we are saving
                                    if (vm.inEditMode) {
                                        vm.setExpandedCourse(null)
                                        println("Save Clicked: \n Size of course list: ${vm.coursesReadOnly.value.size}, \n Course currently expanded should be null : ${vm.expandedCourseNumberReadOnly.value}")

                                    }

                                    vm.setEditMode(!vm.inEditMode)
                                }, modifier = Modifier.fillMaxWidth().weight(1f)

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
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            ) {

                                Text("Remove")
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

                ClickableCourse(
                    vm = vm,
                    course = courseItem,
                    onItemClick = { clickedCourse ->
                        vm.setExpandedCourse(clickedCourse.courseNumber)
                        vm.updateAllFields(clickedCourse.courseNumber, clickedCourse.courseDepartment, clickedCourse.courseLocation)



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
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(), actions = {
                Row {
                    Modifier.fillMaxWidth()
                    Button(
                        onClick = {
                            vm.clearFields()
                            vm.addCourse(
                                vm.INVALIDCN, "", ""
                            )
                            vm.setEditMode(true)
                            vm.setExpandedCourse(vm.INVALIDCN)
                        }, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)

                    ) {
                        Text(text = "Add New", fontSize = 18.sp)
                    }
                }
            })
    }
}