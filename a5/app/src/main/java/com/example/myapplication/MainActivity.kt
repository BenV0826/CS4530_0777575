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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.MarbleViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            MyApplicationTheme {
                val vm : MarbleViewModel = viewModel(factory = MarbleViewModel.Factory)
                MarbleScreen(vm)
            }
        }
    }


    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun MarbleScreen(viewModel : MarbleViewModel){
        BoxWithConstraints (modifier = Modifier.fillMaxSize()){
            val gyroReading by viewModel.gyroReading.collectAsStateWithLifecycle()


        }
    }

}



