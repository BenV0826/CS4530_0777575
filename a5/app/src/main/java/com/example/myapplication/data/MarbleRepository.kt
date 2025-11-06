package com.example.myapplication.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

// Renamed for better reusability
data class SensorReading(val x: Float, val y : Float, val z : Float)


class MarbleRepository (private val sensorManager : SensorManager){

    fun getGyroFlow() : Flow<SensorReading> = channelFlow{
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            return@channelFlow
        }

        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit


            override fun onSensorChanged(event : SensorEvent) {
                // 2. Send the new data class
                trySendBlocking(SensorReading(event.values[0], event.values[1], event.values[2]))

            }
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensorManager.unregisterListener(listener) }

    }

}
