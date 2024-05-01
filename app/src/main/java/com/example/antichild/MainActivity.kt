package com.example.antichild

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.antichild.databinding.ActivityMainBinding
import com.example.antichild.sensors.AccelerometerSensor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accelerometerSensor: AccelerometerSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSensor()
        startAccelerometer()
    }

    override fun onDestroy() {
        killSensors()
        super.onDestroy()
    }

    //функції роботи з сенсором
    private fun initSensor() {
        accelerometerSensor = AccelerometerSensor(applicationContext)
        startAccelerometer()
    }

    private fun startAccelerometer() {
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { a ->
            val result: String? = java.lang.String.format("x: %.4f   y: %.4f   z: %.4f", a[0], a[1], a[2])
            binding.accRes.text = result
            //перевірка руху
        }
    }

    private fun killSensors() {
        accelerometerSensor.stopListening()
    }
}