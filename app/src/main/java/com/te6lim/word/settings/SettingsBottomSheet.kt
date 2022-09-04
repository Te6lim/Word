package com.te6lim.word.settings

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.bottomsheet_settings, container, false
        )

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}