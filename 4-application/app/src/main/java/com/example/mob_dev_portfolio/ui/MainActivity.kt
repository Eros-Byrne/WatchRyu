package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel

/**
 * MainActivity using ViewBinding as required by the assignment to avoid findViewById().
 * This activity handles the UI and observes the ViewModel for data updates.
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding instance
    private lateinit var binding: ActivityMainBinding
    
    // Adapter instance
    private lateinit var adapter: AnimeAdapter
    
    // ViewModel initialization using the 'by viewModels()' delegate
    // This ensures the ViewModel is scoped to this Activity and survives rotation.
    private val viewModel: AnimeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter { anime ->
            viewModel.toggleFavorite(anime)
        }
        
        // Setting up RecyclerView with a LinearLayoutManager
        binding.animeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.animeRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        // Show progress bar while waiting for data (simulated)
        binding.progressBar.visibility = View.VISIBLE

        // Observe the LiveData from the ViewModel
        viewModel.animeList.observe(this) { animeList ->
            binding.progressBar.visibility = View.GONE
            // Update the adapter with the new list
            adapter.submitList(animeList)
        }
    }
}
