package com.example.myapplication.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.MarbleApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarbleViewModel (repository : MarbleRepository) : ViewModel(){

    private val _gyroReading = MutableStateFlow(SensorReading(0f, 0f, 0f))
    val gyroReading : StateFlow<SensorReading> get() =_gyroReading

    init {
        viewModelScope.launch {
            repository.getGyroFlow().collect { newSensorReading ->
                _gyroReading.value = newSensorReading
            }
        }
    }
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MarbleApp)
                MarbleViewModel(application.marbleRepository)
            }
        }
    }

}