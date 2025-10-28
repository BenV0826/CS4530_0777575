package com.example.assignment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment3.data.local.Course
import com.example.assignment3.ui.theme.Assignment3Theme

class MainActivity : ComponentActivity() {

    private val vm: MyViewModel by viewModels {
        val repository = (application as CourseApplication).repository
        ViewModelFactory(repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment3Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(), bottomBar = {
                        MyBottomBar(vm)
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

    @Composable
    fun ClickableCourse(
        course: Course,
        onItemClick: (Course) -> Unit,
        vm: MyViewModel
    ) {
        val isExpanded = course.courseNumber == vm.expandedCourseNumberReadOnly.collectAsState().value

        val courseNumberForTextField by vm.courseNumberReadOnly.collectAsState()
        val courseDepartmentForTextField by vm.courseDeptReadOnly.collectAsState()
        val courseLocationForTextField by vm.courseLocationReadOnly.collectAsState()

        val elevation = animateDpAsState(if (isExpanded) 8.dp else 2.dp, label = "elevation_card")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.value),
            onClick = {
                if (vm.inEditMode) vm.setEditMode(false)
                if (vm.expandedCourseNumberReadOnly.value == course.courseNumber) {
                    vm.setExpandedCourse(null)
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
                        // Check for INVALIDCN before displaying the course text
                        if (course.courseNumber != vm.INVALIDCN) {
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
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                value = courseNumberForTextField,
                                onValueChange = { newNumber ->
                                    vm.updateNumberField(newNumber)
                                },
                                label = { Text("Course Number") }
                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                value = courseDepartmentForTextField,
                                onValueChange = { newDept ->
                                    vm.updateDeptField(newDept)
                                },
                                label = { Text("Course Department") }
                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
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
                                value = course.courseLocation.ifBlank { "N/A" }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    if (vm.inEditMode) {
                                        vm.upsertCourse(
                                            courseNumberForTextField,
                                            courseDepartmentForTextField,
                                            courseLocationForTextField
                                        )
                                        vm.setExpandedCourse(null)
                                    }
                                    vm.setEditMode(!vm.inEditMode)
                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Text(if (vm.inEditMode) "Save" else "Edit Details")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.removeCourse(course) },
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
        val expandedCourseNumber by vm.expandedCourseNumberReadOnly.collectAsState()

        if (courses.isEmpty() && expandedCourseNumber != vm.INVALIDCN) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Text(text = "No courses to display")
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (expandedCourseNumber == vm.INVALIDCN) {
                item {
                    ClickableCourse(
                        vm = vm,
                        course = Course(vm.INVALIDCN, "", ""),
                        onItemClick = {  }
                    )
                }
            }
            items(
                items = courses,
                key = { course -> course.courseNumber }
            ) { courseItem ->
                ClickableCourse(
                    vm = vm,
                    course = courseItem,
                    onItemClick = { clickedCourse ->
                        vm.setExpandedCourse(clickedCourse.courseNumber)
                        vm.updateAllFields(clickedCourse.courseNumber, clickedCourse.courseDepartment, clickedCourse.courseLocation)
                    }
                )
            }
        }
    }

    @Composable
    fun MyBottomBar(vm: MyViewModel) {
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(), actions = {
                Row {
                    Modifier.fillMaxWidth()
                    Button(
                        onClick = {
                            vm.clearFields()
                            vm.setEditMode(true)
                            vm.setExpandedCourse(vm.INVALIDCN)
                        }, modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                    ) {
                        Text(text = "Add New", fontSize = 18.sp)
                    }
                }
            }
        )
    }
}
