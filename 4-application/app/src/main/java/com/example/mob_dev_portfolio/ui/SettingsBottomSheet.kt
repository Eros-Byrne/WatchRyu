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
 * BottomSheet that allows users to adjust accessibility settings.
 * I used a BottomSheet because it feels more modern than a standard settings activity.
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

        // Observe current settings to check the correct buttons
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

        // Click listeners to save new settings
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
