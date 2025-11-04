package com.example.myapplication
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Px
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.MarbleViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            MyApplicationTheme {
                Scaffold (
                ) { innerPadding ->
                    val vm : MarbleViewModel = viewModel(factory = MarbleViewModel.Factory)
                    MarbleScreen(vm, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }


    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun MarbleScreen(viewModel : MarbleViewModel, modifier : Modifier = Modifier) {


        Column(modifier = Modifier.padding(8.dp)) {
            val gyroReading by viewModel.gyroReading.collectAsStateWithLifecycle()

            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .weight(0.8f)
                    .padding(20.dp)
                    .border(1.dp, Color.Black)
            ) {
                // Define the marble size once
                val marbleSize = 50.dp
                val marbleSizePx = with(LocalDensity.current) { marbleSize.toPx() }

                val marbleOffset = remember {
                    mutableStateOf(
                        IntOffset(
                            x = (constraints.maxWidth / 2f - marbleSizePx / 2f).roundToInt(),
                            y = (constraints.maxHeight / 2f - marbleSizePx / 2f).roundToInt()
                        )
                    )
                }


                LaunchedEffect(gyroReading) {

                    val dx = gyroReading.z * 20
                    val dy = gyroReading.x * 20

                    val newX = (marbleOffset.value.x + dx).coerceIn(
                        0f,
                        constraints.maxWidth - marbleSizePx
                    )
                    val newY = (marbleOffset.value.y + dy).coerceIn(
                        0f,
                        constraints.maxHeight - marbleSizePx
                    )

                    marbleOffset.value = IntOffset(newX.roundToInt(), newY.roundToInt())
                }


                Marble(
                    modifier = Modifier.offset { marbleOffset.value },
                    marbleSize = marbleSize
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.2f)
                    .padding(horizontal = 24.dp), // Added padding for better alignment
                text = "Gyro readings:\nx=${"%.2f".format(gyroReading.x)}, y=${"%.2f".format(gyroReading.y)}, z=${"%.2f".format(gyroReading.z)}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
            )
        }
    }





    @Composable
    fun Marble(modifier : Modifier = Modifier, marbleSize : Dp) {
    Box(modifier = modifier
        .size(marbleSize)
        .clip(CircleShape)
        .background(Color.Red)
    )
    }

}



