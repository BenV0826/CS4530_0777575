package com.example.myapplication.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

data class GyroReading(val x: Float, val y : Float, val z : Float)


class MarbleRepository (private val sensorManager : SensorManager){

    fun getGyroFlow() : Flow<GyroReading> = channelFlow{
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroscope == null) return@channelFlow

        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit


            override fun onSensorChanged(event : SensorEvent) {
                trySendBlocking(GyroReading(event.values[0], event.values[1], event.values[2]))

            }
        }

        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }

    }

}

