package com.example.antichild.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class MySensor(
    private val context: Context,
    sensorFeature: String,
    val sensorType: Int
): SensorEventListener {
    private val doesSensorExist: Boolean = context.packageManager.hasSystemFeature(sensorFeature)

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    private var onSensorValuesChanged: ((FloatArray) -> Unit)? = null

    fun startListening() {
        if(!doesSensorExist) {
            return
        }
        if(!::sensorManager.isInitialized && sensor == null) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(this.sensorType)
        }
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        if(!doesSensorExist || !::sensorManager.isInitialized) {
            return
        }
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(!doesSensorExist) {
            return
        }
        if(event?.sensor?.type == sensorType) {
            onSensorValuesChanged?.invoke(event.values)
        }
    }

    fun setOnSensorValuesChangedListener(listener: (FloatArray) -> Unit) {
        onSensorValuesChanged = listener
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}

