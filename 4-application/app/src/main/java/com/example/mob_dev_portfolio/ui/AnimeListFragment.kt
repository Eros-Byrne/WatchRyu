package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.databinding.FragmentAnimeListBinding
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel

/**
 * Fragment that displays a filtered list from the user's local database.
 */
class AnimeListFragment : Fragment() {

    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()
    private lateinit var adapter: AnimeAdapter
    private lateinit var status: AnimeStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = AnimeStatus.valueOf(arguments?.getString(ARG_STATUS) ?: AnimeStatus.WATCHING.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AnimeAdapter(
            onTrackClick = { /* Open edit dialog */ },
            onSaveReview = { anime, review -> 
                val updatedAnime = anime.copy(personalReview = review)
                viewModel.updateAnimeInList(updatedAnime)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.getAnimeByStatus(status).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STATUS = "arg_status"

        fun newInstance(status: AnimeStatus): AnimeListFragment {
            val fragment = AnimeListFragment()
            val args = Bundle()
            args.putString(ARG_STATUS, status.name)
            fragment.arguments = args
            return fragment
        }
    }
}
