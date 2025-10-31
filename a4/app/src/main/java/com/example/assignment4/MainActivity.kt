package com.example.assignment4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.assignment4.ui.FunFactViewModel
import com.example.assignment4.ui.FunFactViewModelProvider
import com.example.assignment4.ui.theme.Assignment4Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         val vm : FunFactViewModel by viewModels{ FunFactViewModelProvider.Factory}

        enableEdgeToEdge()
        setContent {
            val allFacts by vm.allFacts.collectAsState(initial = emptyList())

            Assignment4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding)
                            .background(Color.LightGray),
                        horizontalAlignment = CenterHorizontally,

                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)) {
                            Button(
                                onClick = {
                                        vm.getFact()
                                },
                                modifier = Modifier.fillMaxWidth().weight(0.8f)
                            ) {
                                Text(text = "Get Fun Fact")
                            }
                            Button(
                                onClick = {
                                        vm.clearFacts()
                                }
                            ){
                                Text("Clear")
                            }
                        }

                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal =4.dp, vertical = 20.dp)
                        , horizontalAlignment = CenterHorizontally
                        , verticalArrangement = Arrangement.spacedBy(8.dp)) {
                           items(items = allFacts) { fact ->
                               Box(modifier = Modifier
                                   .fillMaxWidth()
                                   .border(2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                                   .padding(8.dp)) {
                                   Text(fact.text)
                               }
                           }
                        }
                    }
                }
            }
        }
    }
}
