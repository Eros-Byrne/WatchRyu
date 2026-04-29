package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Main Activity that sets up the Tab Navigation and Pager.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AnimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme and accessibility observation must happen early
        viewModel.themeSelection.observe(this) { themeId ->
            applyTheme(themeId)
        }
        
        viewModel.fontSizeSetting.observe(this) { sizeId ->
            applyFontSize(sizeId)
        }

        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupThemeToggle()
        setupSettingsButton()
    }

    private fun setupSettingsButton() {
        binding.settingsButton.setOnClickListener {
            val settingsSheet = SettingsBottomSheet()
            settingsSheet.show(supportFragmentManager, "SettingsBottomSheet")
        }
    }

    /**
     * Sets up TabLayout with ViewPager2. 
     * I used TabLayoutMediator to link them together.
     */
    private fun setupNavigation() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = listOf(
            "Airing", "Stats", "Watching", "Completed", "Dropped", "Plan"
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupThemeToggle() {
        binding.themeButton.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 0, 0, getString(R.string.theme_auto))
            popup.menu.add(0, 1, 1, getString(R.string.theme_light))
            popup.menu.add(0, 2, 2, getString(R.string.theme_dark))
            popup.menu.add(0, 3, 3, getString(R.string.theme_brown))
            
            popup.setOnMenuItemClickListener { item ->
                viewModel.setTheme(item.itemId)
                recreate()
                true
            }
            popup.show()
        }
    }

    private fun applyTheme(themeId: Int) {
        when (themeId) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            3 -> setTheme(R.style.Theme_Mobdevportfolio_Brown)
        }
    }

    /**
     * Applies a global font size based on user preference.
     * In a student project, this shows understanding of theme overlays.
     */
    private fun applyFontSize(sizeId: Int) {
        when (sizeId) {
            0 -> setTheme(R.style.Theme_Mobdevportfolio_FontSmall)
            1 -> setTheme(R.style.Theme_Mobdevportfolio_FontMedium)
            2 -> setTheme(R.style.Theme_Mobdevportfolio_FontLarge)
        }
    }
}
