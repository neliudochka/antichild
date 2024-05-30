package com.example.antichild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.antichild.databinding.FragmentParentMotionDetectionBinding
import com.example.antichild.utils.SharedPreferencesHelper
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

    private fun setButtonListeners() {
        red = requireContext().getColor(R.color.red)

        binding.motionAlarmActivationButton.setBackgroundColor(red)
        binding.motionAlarmActivationButton.setOnClickListener {
            switchActivateStopButtons()
        }

        binding.stop.setBackgroundColor(red)
        binding.stop.setOnClickListener{
            if (binding.passwordEditText.text.toString() == SharedPreferencesHelper.getUserData().parentAccessPassword) {
                binding.movementDetectionTextview.text = resources.getText(R.string.no_movement_detected)
                switchActivateStopButtons()
            } else {
                Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
            }
        }
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