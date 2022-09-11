package com.te6lim.word.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.te6lim.word.R
import com.te6lim.word.databinding.BottomsheetSettingsBinding

class SettingsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "settingsBottomSheet"
    }

    private lateinit var binding: BottomsheetSettingsBinding

    private var settingsItemListener: SettingsItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        retainInstance = true
        binding = DataBindingUtil.inflate(
            inflater, R.layout.bottomsheet_settings, container, false
        )

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> binding.darkThemeSwitch.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> binding.darkThemeSwitch.isChecked = false
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsItemListener?.onThemeSelected(isChecked)
        }

        return binding.root
    }

    fun setItemListener(listener: SettingsItemListener) {
        settingsItemListener = listener
    }

    interface SettingsItemListener {

        fun onThemeSelected(isDarkTheme: Boolean)
    }
}