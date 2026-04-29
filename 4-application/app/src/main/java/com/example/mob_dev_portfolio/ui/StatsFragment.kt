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
 * Fragment that displays detailed user statistics and MAL import options.
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
        
        setupStatLabels()
        setupImport()
        observeStats()
    }

    private fun setupStatLabels() {
        binding.statWatching.statLabel.text = getString(R.string.stat_watching)
        binding.statCompleted.statLabel.text = getString(R.string.stat_completed)
        binding.statOnHold.statLabel.text = getString(R.string.stat_on_hold)
        binding.statDropped.statLabel.text = getString(R.string.stat_dropped)
        binding.statPlanToWatch.statLabel.text = getString(R.string.stat_plan_to_watch)
        binding.statTotalEntries.statLabel.text = getString(R.string.stat_total_entries)
        binding.statRewatched.statLabel.text = getString(R.string.stat_rewatched)
        binding.statEpisodes.statLabel.text = getString(R.string.stat_episodes)
    }

    private fun setupImport() {
        binding.importButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            if (username.isNotEmpty()) {
                Toast.makeText(context, "Importing list for $username...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeStats() {
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            // 1. Group by status for the Pie Chart
            val statusCounts = list.groupingBy { it.status }.eachCount()
            
            val entries = mutableListOf<PieEntry>()
            statusCounts.forEach { (status, count) ->
                if (status != AnimeStatus.AIRING) {
                    entries.add(PieEntry(count.toFloat(), status.name.replace("_", " ")))
                }
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 12f

            binding.pieChart.apply {
                data = PieData(dataSet)
                centerText = "Library\nBreakdown"
                setCenterTextSize(16f)
                description.isEnabled = false
                legend.isEnabled = false
                animateY(800)
                invalidate()
            }

            // 2. Set individual grid values
            binding.statWatching.statValue.text = (statusCounts[AnimeStatus.WATCHING] ?: 0).toString()
            binding.statCompleted.statValue.text = (statusCounts[AnimeStatus.COMPLETED] ?: 0).toString()
            binding.statOnHold.statValue.text = (statusCounts[AnimeStatus.ON_HOLD] ?: 0).toString()
            binding.statDropped.statValue.text = (statusCounts[AnimeStatus.DROPPED] ?: 0).toString()
            binding.statPlanToWatch.statValue.text = (statusCounts[AnimeStatus.PLAN_TO_WATCH] ?: 0).toString()
            
            binding.statTotalEntries.statValue.text = list.size.toString()
            binding.statRewatched.statValue.text = list.sumOf { it.rewatchCount }.toString()
            
            val totalEpisodes = list.sumOf { it.episodesWatched }
            binding.statEpisodes.statValue.text = totalEpisodes.toString()

            // 3. Calculate Time Watched (Roughly 24 mins per episode)
            val totalMinutes = totalEpisodes * 24L
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
