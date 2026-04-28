package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.databinding.FragmentAiringBinding
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel

/**
 * Fragment that displays currently airing anime from the Jikan API.
 * Users can add shows from here to their personal tracking lists.
 */
class AiringFragment : Fragment() {

    private var _binding: FragmentAiringBinding? = null
    private val binding get() = _binding!!
    
    // Sharing the ViewModel with MainActivity
    private val viewModel: AnimeViewModel by activityViewModels()
    private lateinit var adapter: AnimeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter { anime ->
            // Handle actions like adding to list
            viewModel.updateAnimeInList(anime)
        }
        binding.airingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.airingRecyclerView.adapter = adapter
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
