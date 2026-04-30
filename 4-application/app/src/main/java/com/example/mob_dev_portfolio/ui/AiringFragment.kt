package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentAiringBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel

/**
 * Enhanced discovery fragment with search and upcoming filters.
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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter(
            onTrackClick = { anime -> viewModel.updateAnimeInList(anime) },
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
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the current list in the adapter
                val currentList = viewModel.animeList.value ?: emptyList()
                if (newText.isNullOrEmpty()) {
                    adapter.submitList(currentList)
                } else {
                    val filtered = currentList.filter { 
                        it.title.contains(newText, ignoreCase = true) 
                    }
                    adapter.submitList(filtered)
                }
                return true
            }
        })
    }

    private fun setupFilters() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                // Show progress bar during fetch
                binding.progressBar.visibility = View.VISIBLE
                when (checkedId) {
                    R.id.btnTop -> viewModel.fetchTopAnime()
                    R.id.btnSeasonal -> viewModel.fetchSeasonalAnime()
                    R.id.btnUpcoming -> viewModel.fetchUpcomingAnime()
                }
            }
        }
    }

    private fun observeViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.animeList.observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
