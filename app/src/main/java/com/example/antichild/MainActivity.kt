package com.example.antichild

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.antichild.databinding.ActivityMainBinding
import com.example.antichild.sensors.AccelerometerSensor
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accelerometerSensor: AccelerometerSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonListeners()
    }

    override fun onDestroy() {
        killSensors()
        super.onDestroy()
    }

    //ui
    private var isSensors = false;
    private var greyColor by Delegates.notNull<Int>()
    private var pinky by Delegates.notNull<Int>()
    private var red by Delegates.notNull<Int>()

    fun setButtonListeners() {
        greyColor = applicationContext.getColor(R.color.gray)
        pinky = applicationContext.getColor(R.color.pinky)
        red = applicationContext.getColor(R.color.red)

        binding.onOffSensors.setBackgroundColor(greyColor)
        binding.onOffSensors.setOnClickListener {
            switchOffSensors()
        }
    }

    private fun switchOffSensors() {
        if(isSensors) {
            isSensors = false
            binding.onOffSensors.setBackgroundColor(greyColor)
            killSensors()
        } else {
            isSensors = true
            binding.onOffSensors.setBackgroundColor(pinky)
            initSensors()
        }
    }

    //функції роботи з сенсором
    private fun initSensors() {
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