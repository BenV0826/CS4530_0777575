package com.example.assignment4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment4.ui.FunFactViewModel
import com.example.assignment4.ui.FunFactViewModelProvider
import com.example.assignment4.ui.theme.Assignment4Theme
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         val vm : FunFactViewModel by viewModels{ FunFactViewModelProvider.Factory}

        enableEdgeToEdge()
        setContent {
            //val facts by vm.allFacts.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val allFacts by vm.allFacts.collectAsState(initial = emptyList())

            Assignment4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.LightGray),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                            coroutineScope.launch {
                                vm.getFact()
                                Log.e("On Click Get Button", vm.allFacts.value.size.toString())

                            }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Get Fun Fact")
                        }

                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.Black)
                            .padding(4.dp)
                        , horizontalAlignment = CenterHorizontally) {
                           items(allFacts) { fact ->
                               Text(fact.text)
                           }
                        }
//                    }
//                }
//            }
                    }
                }
            }
        }
    }
}
