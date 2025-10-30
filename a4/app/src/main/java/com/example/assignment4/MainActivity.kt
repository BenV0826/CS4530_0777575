package com.example.assignment4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment4.ui.theme.Assignment4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.LightGray),
                        horizontalAlignment = CenterHorizontally
                        ) {
                        Button(onClick = {
                            // placeholder
                        },
                            modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Get Fun Fact")
                        }

                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.Black)
                            .padding(4.dp)
                        , horizontalAlignment = CenterHorizontally) {
                            item {
                                Text("PlaceHolder")
                            }
                        }
                    }
                }
            }
        }
    }
}

