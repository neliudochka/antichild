package com.example.antichild

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.antichild.databinding.FragmentParentMotionDetectionBinding
import com.example.antichild.notification.NotificationService
import kotlin.properties.Delegates

class ParentMotionDetectionFragment : Fragment() {
    private lateinit var binding: FragmentParentMotionDetectionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentMotionDetectionBinding.inflate(inflater, container, false)

        setButtonListeners()

        return binding.root
    }

    //ui
    private var isActivated = false
    private var red by Delegates.notNull<Int>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isActivated", isActivated)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            isActivated = savedInstanceState.getBoolean("isActivated")
            switchActivateStopButtons()
        }
    }


    private fun setButtonListeners() {
        red = requireContext().getColor(R.color.red)

        binding.motionAlarmActivationButton.setBackgroundColor(red)
        binding.motionAlarmActivationButton.setOnClickListener {
            switchActivateStopButtons()
            startNotificationService()
        }

        binding.stop.setBackgroundColor(red)
        binding.stop.setOnClickListener{
/*            if (binding.passwordEditText.text.toString() == SharedPreferencesHelper.getParentData().accessPassword) {
                binding.movementDetectionTextview.text = resources.getText(R.string.no_movement_detected)
                switchActivateStopButtons()
            } else {
                Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
            }*/
            switchActivateStopButtons()
            stopNotificationService()
        }
    }

    private fun startNotificationService() {
        val serviceIntent = Intent(requireContext(), NotificationService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }

    private fun stopNotificationService() {
        val serviceIntent = Intent(requireContext(), NotificationService::class.java)
        requireContext().stopService(serviceIntent)
    }

    private fun switchActivateStopButtons() {
        if(isActivated) {
            //вимкнути аларм на телефоні чайлда
            isActivated = false
            binding.motionAlarmActivationButton.visibility = View.VISIBLE
            binding.stop.visibility = View.INVISIBLE
            binding.passwordEditText.visibility = View.INVISIBLE
        } else {
            //ввімкнути аларм на телефоні чайлда
            isActivated = true
            binding.motionAlarmActivationButton.visibility = View.INVISIBLE
            binding.stop.visibility = View.VISIBLE
            binding.passwordEditText.visibility = View.VISIBLE
        }
    }

    companion object {
            @JvmStatic
            fun newInstance() = ParentMotionDetectionFragment()
        }
}