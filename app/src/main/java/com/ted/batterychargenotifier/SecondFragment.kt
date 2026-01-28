package com.ted.batterychargenotifier

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ted.batterychargenotifier.databinding.FragmentSecondBinding
import androidx.core.content.edit

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val PREFS_NAME = "BatteryPrefs"
    private val KEY_STARTUP_NOTIFY_ENABLED = "startup_notify_enabled"
    private val KEY_MESSAGE = "message"
    private val KEY_THRESHOLD = "threshold"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()

        binding.btnSave.setOnClickListener {
            saveSettings()
            findNavController().navigateUp()
        }
    }

    private fun loadSettings() {
        val settings: SharedPreferences =
            requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.switchStartupNotify.isChecked = settings.getBoolean(KEY_STARTUP_NOTIFY_ENABLED, true)
        binding.etMessage.setText(settings.getString(KEY_MESSAGE, "请注意电量") ?: "请注意电量")
        binding.etThreshold.setText(settings.getString(KEY_THRESHOLD, "80") ?: "80")
    }

    private fun saveSettings() {
        val rawThreshold = binding.etThreshold.text?.toString()?.trim().orEmpty()
        val threshold = rawThreshold.toIntOrNull()?.coerceIn(0, 100)?.toString() ?: "80"
        val message = binding.etMessage.text?.toString()?.trim().orEmpty().ifBlank { "请注意电量" }

        requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_STARTUP_NOTIFY_ENABLED, binding.switchStartupNotify.isChecked)
            putString(KEY_MESSAGE, message)
            putString(KEY_THRESHOLD, threshold)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
