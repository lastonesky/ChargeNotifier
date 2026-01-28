package com.ted.batterychargenotifier

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ted.batterychargenotifier.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
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
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun loadSettings() {
        val settings: SharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val startupNotifyEnabled = settings.getBoolean(KEY_STARTUP_NOTIFY_ENABLED, true)
        val message = settings.getString(KEY_MESSAGE, "请注意电量") ?: "请注意电量"
        val threshold = settings.getString(KEY_THRESHOLD, "80") ?: "80"

        binding.tvStartupNotifyEnabled.text = getString(
            R.string.startup_notify_enabled_value,
            if (startupNotifyEnabled) getString(R.string.enabled) else getString(R.string.disabled)
        )
        binding.tvNotifyMessage.text = getString(R.string.notify_message_value, message)
        binding.tvThreshold.text = getString(R.string.threshold_value, threshold)
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
