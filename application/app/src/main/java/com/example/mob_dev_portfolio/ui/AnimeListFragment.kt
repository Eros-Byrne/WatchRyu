package com.example.mob_dev_portfolio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mob_dev_portfolio.databinding.FragmentAnimeListBinding
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.util.ExportHelper
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import java.io.OutputStreamWriter

/**
 * Fragment that displays a filtered list from the user's local database.
 */
class AnimeListFragment : Fragment() {

    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()
    private lateinit var adapter: AnimeAdapter
    private lateinit var status: AnimeStatus

    // Exporter for single list (CSV)
    private val csvExporter = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let {
            viewModel.getAnimeByStatus(status).observe(viewLifecycleOwner) { list ->
                try {
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(ExportHelper.convertToCsv(list))
                    writer.close()
                    Toast.makeText(context, "List exported as CSV!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
        setupExport()
        observeViewModel()
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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupExport() {
        binding.exportButton.setOnClickListener {
            csvExporter.launch("${status.name}_List.csv")
        }
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
