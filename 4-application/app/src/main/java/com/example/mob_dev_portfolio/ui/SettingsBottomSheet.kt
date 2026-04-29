package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.BottomSheetSettingsBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Unified Settings Panel.
 * I consolidated Theme, Contrast, and Font size here to keep the main UI clean.
 */
class SettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initial UI States from DataStore
        viewModel.themeSelection.observe(viewLifecycleOwner) { theme ->
            when (theme) {
                1 -> binding.themeToggleGroup.check(R.id.btnThemeLight)
                2 -> binding.themeToggleGroup.check(R.id.btnThemeDark)
                3 -> binding.themeToggleGroup.check(R.id.btnThemeBrown)
            }
        }

        viewModel.contrastSetting.observe(viewLifecycleOwner) { contrast ->
            if (contrast == 1) {
                binding.contrastToggleGroup.check(R.id.btnHighContrast)
            } else {
                binding.contrastToggleGroup.check(R.id.btnNormalContrast)
            }
        }

        viewModel.fontSizeSetting.observe(viewLifecycleOwner) { size ->
            when (size) {
                0 -> binding.fontSizeGroup.check(R.id.radioSmall)
                1 -> binding.fontSizeGroup.check(R.id.radioMedium)
                2 -> binding.fontSizeGroup.check(R.id.radioLarge)
            }
        }

        // 2. Click Listeners
        binding.btnThemeLight.setOnClickListener { viewModel.setTheme(1) }
        binding.btnThemeDark.setOnClickListener { viewModel.setTheme(2) }
        binding.btnThemeBrown.setOnClickListener { viewModel.setTheme(3) }

        binding.btnNormalContrast.setOnClickListener { viewModel.setContrast(0) }
        binding.btnHighContrast.setOnClickListener { viewModel.setContrast(1) }

        binding.radioSmall.setOnClickListener { viewModel.setFontSize(0) }
        binding.radioMedium.setOnClickListener { viewModel.setFontSize(1) }
        binding.radioLarge.setOnClickListener { viewModel.setFontSize(2) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
