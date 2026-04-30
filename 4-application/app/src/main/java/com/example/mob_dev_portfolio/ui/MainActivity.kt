package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Main Activity for WatchRyu.
 * Handles the multi-layered theme application (Theme + Contrast + Font).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AnimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // We MUST fetch initial settings synchronously because DataStore/LiveData is async.
        // If we don't, the app starts with Light mode for one frame and then flickers.
        val initialSettings = viewModel.getInitialSettings()
        val theme = initialSettings.first
        val font = initialSettings.second
        val contrast = initialSettings.third

        // Apply layers BEFORE super.onCreate
        applyGlobalStyles(theme, font, contrast)

        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe for real-time changes from the Settings panel
        viewModel.themeSelection.observe(this) { if (it != theme) recreate() }
        viewModel.fontSizeSetting.observe(this) { if (it != font) recreate() }
        viewModel.contrastSetting.observe(this) { if (it != contrast) recreate() }

        setupNavigation()
        setupSettingsButton()
    }

    private fun setupNavigation() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        val tabTitles = listOf("Airing", "Stats", "Watching", "Completed", "On-Hold", "Dropped", "Plan")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos -> tab.text = tabTitles[pos] }.attach()
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            SettingsBottomSheet().show(supportFragmentManager, "Settings")
        }
    }

    /**
     * Applies themes in the correct order so they overlay properly.
     */
    private fun applyGlobalStyles(themeId: Int, fontSizeId: Int, contrastId: Int) {
        // 1. Base Theme Selection
        when (themeId) {
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_WatchRyu)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_WatchRyu)
            }
            3 -> {
                // Force Light mode resources for the custom brown palette to prevent clashing
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_WatchRyu_Brown)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                setTheme(R.style.Theme_WatchRyu)
            }
        }

        // 2. High Contrast Layer
        if (contrastId == 1) {
            val isDark = themeId == 2 || (themeId == 0 && isSystemInDarkMode())
            if (isDark) {
                setTheme(R.style.Theme_WatchRyu_HighContrast_Dark)
            } else {
                setTheme(R.style.Theme_WatchRyu_HighContrast)
            }
        }

        // 3. Font Size Layer
        when (fontSizeId) {
            0 -> setTheme(R.style.Theme_WatchRyu_FontSmall)
            2 -> setTheme(R.style.Theme_WatchRyu_FontLarge)
            else -> setTheme(R.style.Theme_WatchRyu_FontMedium)
        }
    }

    private fun isSystemInDarkMode(): Boolean {
        val uiMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
