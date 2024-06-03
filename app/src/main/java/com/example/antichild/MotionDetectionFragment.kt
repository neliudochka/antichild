package com.example.antichild

import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.antichild.auth.LaunchFragment
import com.example.antichild.databinding.FragmentMotionDetectionBinding
import com.example.antichild.sensors.AccelerometerSensor
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Locale
import kotlin.math.sqrt
import kotlin.properties.Delegates
import android.content.Intent
import com.example.antichild.utils.SharedPreferencesHelper

class MotionDetectionFragment : Fragment() {
    private lateinit var binding: FragmentMotionDetectionBinding
    private lateinit var accelerometerSensor: AccelerometerSensor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMotionDetectionBinding.inflate(inflater, container, false)

        setButtonListeners()

        return binding.root
    }

    override fun onDestroy() {
        if (::accelerometerSensor.isInitialized) {
            killSensors()
        }
        super.onDestroy()
    }

    //ui
    private var isActivated = false
    private var isSensors = false
    private var greyColor by Delegates.notNull<Int>()
    private var pinky by Delegates.notNull<Int>()
    private var red by Delegates.notNull<Int>()

    private fun setButtonListeners() {
        greyColor = requireContext().getColor(R.color.gray)
        pinky = requireContext().getColor(R.color.pinky)
        red = requireContext().getColor(R.color.red)

        binding.motionAlarmActivationButton.setBackgroundColor(red)
        binding.motionAlarmActivationButton.setOnClickListener {
            switchActivateStopButtons()
        }

        binding.stop.setBackgroundColor(red)
        binding.stop.setOnClickListener{
            if (binding.passwordEditText.text.toString() == SharedPreferencesHelper.getChildData().accessPassword) {
                isStolen = false
                binding.movementDetectionTextview.text = resources.getText(R.string.no_movement_detected)
                stopMusicService()
                switchActivateStopButtons()
            } else {
                Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchActivateStopButtons() {
        if(isActivated) {
            isActivated = false
            binding.motionAlarmActivationButton.visibility = View.VISIBLE
            binding.stop.visibility = View.INVISIBLE
            binding.passwordEditText.visibility = View.INVISIBLE
        } else {
            isActivated = true
            switchOnOffSensors()
            binding.motionAlarmActivationButton.visibility = View.INVISIBLE
            binding.stop.visibility = View.VISIBLE
            binding.passwordEditText.visibility = View.VISIBLE
        }
    }
    private fun switchOnOffSensors() {
        if(isSensors) {
            isSensors = false
            killSensors()
        } else {
            isSensors = true
            initSensors()
        }
    }

    //функції роботи з сенсором
    private fun initSensors() {
        accelerometerSensor = AccelerometerSensor(requireContext())
        startAccelerometer()
    }

    private fun startAccelerometer() {
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { a ->
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
            startMusicService()
            switchOnOffSensors()
        }
    }

    //music Service
    private fun startMusicService() {
        val intent = Intent(context, MusicService::class.java)
        activity?.startService(intent)
    }

    private fun stopMusicService() {
        activity?.stopService(Intent(context, MusicService::class.java))
    }
    companion object {
        @JvmStatic
        fun newInstance() = MotionDetectionFragment()
    }
}