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

        binding.btnSave.setOnClickListener {
            saveSettings()
        }
    }
    private fun saveSettings() {
        with(requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()) {
            putString("threshold", binding.etThreshold.text.toString())
            putString("message", binding.etMessage.text.toString())
            apply()
        }
    }

    private fun loadSettings() {
        val settings: SharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        binding.etThreshold.setText(settings.getString("threshold", ""))
        binding.etMessage.setText(settings.getString("message", ""))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}