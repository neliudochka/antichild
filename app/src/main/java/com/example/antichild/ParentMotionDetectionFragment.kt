package com.example.antichild

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.antichild.databinding.FragmentParentMotionDetectionBinding
import com.example.antichild.notification.MotionAlarmNotification
import com.example.antichild.notification.NotificationService
import com.example.antichild.utils.SharedPreferencesHelper
import kotlin.properties.Delegates

class ParentMotionDetectionFragment : Fragment() {
    private lateinit var binding: FragmentParentMotionDetectionBinding
    private lateinit var motionAlarmNotification: MotionAlarmNotification
    private var isActivated = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentMotionDetectionBinding.inflate(inflater, container, false)

        motionAlarmNotification = MotionAlarmNotification(requireContext())
        initUI()
        setButtonListeners()

        return binding.root
    }

    private fun initUI() {
        isActivated = SharedPreferencesHelper.getParentButtonState()
        if(!isActivated) {
            binding.motionAlarmActivationButton.visibility = View.VISIBLE
            binding.stop.visibility = View.INVISIBLE
        } else {
            binding.motionAlarmActivationButton.visibility = View.INVISIBLE
            binding.stop.visibility = View.VISIBLE
        }

        val showPasswordDialog = arguments?.getBoolean("showPasswordDialog", false) ?: false
        if (showPasswordDialog) {
            Log.d("ParentMotionDetectionFragment", showPasswordDialog.toString())
            showPasswordPrompt()
        }
    }

    private fun showPasswordPrompt() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_password, null)

        builder.setView(view)
            .setPositiveButton("OK") { dialog, _ ->
                val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
                val password = passwordEditText.text.toString()
                Log.d("ParentMotionDetectionFragment", password)
                if (validatePassword(password)) {
                    val childUid = SharedPreferencesHelper.getCurrentChild()
                    motionAlarmNotification.createParentRecord(childUid!!)
                } else {
                    Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            false
        } else password == SharedPreferencesHelper.getParentData().accessPassword
    }

    //ui
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
        } else {
            //ввімкнути аларм на телефоні чайлда
            isActivated = true
            binding.motionAlarmActivationButton.visibility = View.INVISIBLE
            binding.stop.visibility = View.VISIBLE
        }

        SharedPreferencesHelper.saveParentButtonState(isActivated)
    }

    companion object {
            @JvmStatic
            fun newInstance(showPasswordDialog: Boolean = false) = ParentMotionDetectionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("showPasswordDialog", showPasswordDialog)
                }
            }
        }
}