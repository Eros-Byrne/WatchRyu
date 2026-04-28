package com.example.mob_dev_portfolio.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentStatsBinding
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * Fragment that displays user statistics using a Pie Chart.
 * Also handles the MAL account import feature.
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupImport()
        observeStats()
    }

    private fun setupImport() {
        binding.importButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            if (username.isNotEmpty()) {
                Toast.makeText(context, "Importing list for $username...", Toast.LENGTH_SHORT).show()
                // TODO: Logic for Jikan User List import
            }
        }
    }

    private fun observeStats() {
        // We observe all anime to calculate counts and time
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            val statusCounts = list.groupingBy { it.status }.eachCount()
            
            val entries = mutableListOf<PieEntry>()
            statusCounts.forEach { (status, count) ->
                entries.add(PieEntry(count.toFloat(), status.name))
            }

            val dataSet = PieDataSet(entries, "Anime Status")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 14f

            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.centerText = getString(R.string.total_label, list.size)
            binding.pieChart.animateY(1000)
            binding.pieChart.invalidate()

            // Calculate total time (roughly)
            // Assuming average episode length is 24 mins
            val totalMinutes = list.sumOf { it.episodesWatched * 24 }
            val days = totalMinutes / (24 * 60)
            val hours = (totalMinutes % (24 * 60)) / 60
            val minutes = totalMinutes % 60
            
            binding.timeSpentValue.text = getString(R.string.time_spent_format, days, hours, minutes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
