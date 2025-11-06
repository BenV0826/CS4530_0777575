package com.example.myapplication
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
                Scaffold { innerPadding ->
                    val vm : MarbleViewModel = viewModel(factory = MarbleViewModel.Factory)
                    MarbleScreen(vm, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }


    @SuppressLint("UnusedBoxWithConstraintsScope", "UnrememberedMutableState")
    @Composable
    fun MarbleScreen(viewModel: MarbleViewModel, modifier: Modifier = Modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            val gyroReading by viewModel.gyroReading.collectAsStateWithLifecycle()

            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .border(1.dp, Color.Black)
            ) {
                val marbleSize = 50.dp
                val marbleSizePx = with(LocalDensity.current) { marbleSize.toPx() }

                val position = remember {
                    mutableStateOf(
                        Offset(
                            x = (constraints.maxWidth / 2f) - (marbleSizePx / 2f),
                            y = (constraints.maxHeight / 2f) - (marbleSizePx / 2f)
                        )
                    )
                }
                val velocity = remember { mutableStateOf(Offset.Zero) }
                val alpha = 0.8f
                val gravity = remember { mutableStateOf<Offset>(Offset.Zero)}
                LaunchedEffect(Unit) {
                    var lastFrameTimeNanos = System.nanoTime()
                     // Used a while loop that constantly calculates the physics of the marble
                    while (true) {
                        withFrameNanos { frameTimeNanos ->
                            val deltaTimeSeconds = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f
                            lastFrameTimeNanos = frameTimeNanos
                            gravity.value = Offset(
                                x = alpha * gravity.value.x + (1 - alpha) * gyroReading.x,
                                y = alpha * gravity.value.y + (1 - alpha) * gyroReading.y
                            )
                            val force = Offset(x = gravity.value.x * -1000f, y = gravity.value.y * 1000f)

                            val friction = velocity.value * -3.5f
                            val acceleration = force + friction

                            velocity.value += acceleration * deltaTimeSeconds

                            val newPosition = position.value + velocity.value * deltaTimeSeconds

                            position.value = Offset(
                                x = newPosition.x.coerceIn(0f, constraints.maxWidth - marbleSizePx),
                                y = newPosition.y.coerceIn(0f, constraints.maxHeight - marbleSizePx)
                            )

                            if (newPosition.y <= 0f || newPosition.y >= constraints.maxHeight - marbleSizePx) {
                                velocity.value = Offset(velocity.value.x, velocity.value.y * -0.4f)
                            }
                            if (newPosition.x <= 0f || newPosition.x >= constraints.maxWidth - marbleSizePx) {
                                velocity.value = Offset(velocity.value.x * -0.4f, velocity.value.y)
                            }
                        }
                    }
                }
                Marble(
                    modifier = Modifier.offset { IntOffset(position.value.x.roundToInt(), position.value.y.roundToInt()) },
                    marbleSize = marbleSize
                )
            }
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



