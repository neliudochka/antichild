package com.example.antichild

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.antichild.databinding.ActivityMainBinding
import com.example.antichild.sensors.AccelerometerSensor
import java.util.Locale
import kotlin.math.sqrt
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
    private var isActivated = false
    private var isSensors = false
    private var greyColor by Delegates.notNull<Int>()
    private var pinky by Delegates.notNull<Int>()
    private var red by Delegates.notNull<Int>()

    private fun setButtonListeners() {
        greyColor = applicationContext.getColor(R.color.gray)
        pinky = applicationContext.getColor(R.color.pinky)
        red = applicationContext.getColor(R.color.red)

        binding.motionAlarmActivationButton.setBackgroundColor(greyColor)
        binding.motionAlarmActivationButton.setOnClickListener {
            if (!isStolen)
                if(isSensors)
                    switchActivatedButton()
                else
                    Toast.makeText(this, "Firstly, turn on the sensors", Toast.LENGTH_SHORT).show()
        }

        binding.onOffSensors.setBackgroundColor(greyColor)
        binding.onOffSensors.setOnClickListener {
            if (!isStolen)
                switchOffSensors()
        }

        binding.reset.setBackgroundColor(red)
        binding.reset.setOnClickListener{
            isStolen = false
            binding.movementDetectionTextview.text = resources.getText(R.string.no_movement_detected)
        }
    }

    private fun switchActivatedButton() {
        if(isActivated) {
            isActivated = false
            binding.motionAlarmActivationButton.setBackgroundColor(greyColor)
        } else {
            isActivated = true
            binding.motionAlarmActivationButton.setBackgroundColor(pinky)
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
            val result: String? = java.lang.String.format(Locale.US, "x: %.4f   y: %.4f   z: %.4f", a[0], a[1], a[2])
            binding.accRes.text = result
            if(isActivated && !isStolen) {
                checkMotion(a.toList())
            }
        }
    }

    private fun killSensors() {
        accelerometerSensor.stopListening()
    }


    //Movement detection
    private var mAccel = 0.00f
    private var mAccelCurrent = SensorManager.GRAVITY_EARTH
    private var mAccelLast = SensorManager.GRAVITY_EARTH

    private var isStolen = false

    private fun checkMotion(values: List<Float>) {
        val x: Float = values[0]
        val y: Float = values[1]
        val z: Float = values[2]
        mAccelLast = mAccelCurrent
        //загальна велечина прискорення =  √x^2+y^2+z^2
        mAccelCurrent = sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
        val delta: Float = mAccelLast - mAccelCurrent
        mAccel = mAccel * 0.9f + delta
        if (mAccel > 0.5) {
            isStolen = true
            binding.movementDetectionTextview.text = resources.getText(R.string.movement_detected)
            switchOffSensors()
            switchActivatedButton()
        }
    }
}