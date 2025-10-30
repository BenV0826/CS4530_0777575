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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment4.data.FunFact
import com.example.assignment4.ui.FunFactViewModel
import com.example.assignment4.ui.FunFactViewModelProvider
import com.example.assignment4.ui.theme.Assignment4Theme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private val client = HttpClient(Android)
    {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }


    var fetched by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm : FunFactViewModel by viewModels{ FunFactViewModelProvider.Factory}
        var fact by mutableStateOf("")

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
                            CoroutineScope(Dispatchers.IO).launch()
                            {
                                try{
                                    val responseText: FunFact = client.get("https://uselessfacts.jsph.pl//api/v2/facts/random").body()
                                    fact = responseText.text
                                }
                                catch (e: Exception)
                                {
                                    Log.e("FunFact Activity", "Error fetching", e)
                                }



//                                vm.getFact()
                            }
                            fetched = true
                        },
                            modifier = Modifier.fillMaxWidth()) {
                            if(fetched){
                                Text(text = fact)
                            }
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

