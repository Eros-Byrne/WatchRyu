package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel

/**
 * MainActivity for the Anime Tracker app.
 * I implemented a theme switcher and a panel-based UI for the anime list.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AnimeAdapter
    private val viewModel: AnimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // We need to check the theme before calling super.onCreate or setContentView.
        // I use the ViewModel to observe the theme stored in DataStore.
        viewModel.themeSelection.observe(this) { themeId ->
            applyTheme(themeId)
        }

        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupThemeToggle()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter { anime ->
            viewModel.toggleFavorite(anime)
        }
        
        binding.animeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.animeRecyclerView.adapter = adapter
    }

    /**
     * This method handles the theme switching logic.
     * I used a PopupMenu to give the user multiple choices.
     */
    private fun setupThemeToggle() {
        binding.themeButton.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add(0, 0, 0, getString(R.string.theme_auto))
            popup.menu.add(0, 1, 1, getString(R.string.theme_light))
            popup.menu.add(0, 2, 2, getString(R.string.theme_dark))
            popup.menu.add(0, 3, 3, getString(R.string.theme_brown))
            
            popup.setOnMenuItemClickListener { item ->
                viewModel.setTheme(item.itemId)
                // We recreate the activity to apply the new theme immediately.
                recreate()
                true
            }
            popup.show()
        }
    }

    /**
     * Applies the selected theme using AppCompatDelegate or custom styles.
     */
    private fun applyTheme(themeId: Int) {
        when (themeId) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            3 -> {
                // For the custom brown theme, we set it manually.
                setTheme(R.style.Theme_Mobdevportfolio_Brown)
            }
        }
    }

    private fun observeViewModel() {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.animeList.observe(this) { animeList ->
            binding.progressBar.visibility = View.GONE
            adapter.submitList(animeList)
        }
    }
}
