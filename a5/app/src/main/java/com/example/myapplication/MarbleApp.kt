package com.example.myapplication

import android.app.Application
import android.hardware.SensorManager
import com.example.myapplication.data.MarbleRepository

class MarbleApp : Application() {

    val sensorManager : SensorManager by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager

    }

    val marbleRepository by lazy {
        MarbleRepository(sensorManager)
    }
}