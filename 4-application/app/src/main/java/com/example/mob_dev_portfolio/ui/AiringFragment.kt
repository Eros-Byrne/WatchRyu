package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentAiringBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.google.android.material.chip.Chip

/**
 * Enhanced discovery fragment with search, category toggles, and genre filters.
 */
class AiringFragment : Fragment() {

    private var _binding: FragmentAiringBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()
    private lateinit var adapter: AnimeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeGenres()
        observeViewModel()

        // Ensure data is loaded if the list is currently empty
        if (viewModel.animeList.value.isNullOrEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.fetchSeasonalAnime()
        }
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter(
            onTrackClick = { anime -> 
                TrackAnimeDialog(anime) { updated -> 
                    viewModel.updateAnimeInList(updated)
                }.show(childFragmentManager, "TrackDialog")
            },
            onSaveReview = { anime, review -> 
                val updatedAnime = anime.copy(personalReview = review)
                viewModel.updateAnimeInList(updatedAnime)
            }
        )
        binding.airingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.airingRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.performSearch(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterAndSubmitList(newText)
                return true
            }
        })
    }

    private fun filterAndSubmitList(query: String? = binding.searchView.query?.toString()) {
        val currentList = viewModel.animeList.value ?: emptyList()
        if (query.isNullOrEmpty()) {
            adapter.submitList(currentList)
        } else {
            val filtered = currentList.filter { 
                it.title.contains(query, ignoreCase = true) 
            }
            adapter.submitList(filtered)
        }
    }

    private fun setupFilters() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                binding.progressBar.visibility = View.VISIBLE
                when (checkedId) {
                    R.id.btnTop -> viewModel.fetchTopAnime()
                    R.id.btnSeasonal -> viewModel.fetchSeasonalAnime()
                    R.id.btnUpcoming -> viewModel.fetchUpcomingAnime()
                }
            }
        }
    }

    /**
     * Populates the ChipGroup with genres from the API.
     */
    private fun observeGenres() {
        viewModel.genres.observe(viewLifecycleOwner) { genreList ->
            if (genreList == null) return@observe
            binding.genreChipGroup.removeAllViews()
            
            // Add an "All" chip
            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
                id = View.generateViewId()
                setOnClickListener { viewModel.onGenreSelected(null) }
            }
            binding.genreChipGroup.addView(allChip)

            genreList.forEach { genre ->
                val chip = Chip(requireContext()).apply {
                    text = genre.name
                    isCheckable = true
                    id = View.generateViewId()
                    setOnClickListener { viewModel.onGenreSelected(genre.id) }
                }
                binding.genreChipGroup.addView(chip)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.animeList.observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE
            filterAndSubmitList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
