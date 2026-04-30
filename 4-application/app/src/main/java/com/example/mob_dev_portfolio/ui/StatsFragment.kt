package com.example.mob_dev_portfolio.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.FragmentStatsBinding
import com.example.mob_dev_portfolio.model.AnimeStatus
import com.example.mob_dev_portfolio.util.ExportHelper
import com.example.mob_dev_portfolio.util.ImportHelper
import com.example.mob_dev_portfolio.viewmodel.AnimeViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.OutputStreamWriter

/**
 * Fragment that displays detailed user statistics and MAL import/export options.
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AnimeViewModel by activityViewModels()

    // File picker for XML import
    private val xmlPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                if (inputStream != null) {
                    val list = ImportHelper.parseMalXml(inputStream)
                    list.forEach { anime -> viewModel.updateAnimeInList(anime) }
                    Toast.makeText(context, "Imported ${list.size} shows from XML", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error parsing XML file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Exporter for Full Account (XML)
    private val accountExporter = registerForActivityResult(ActivityResultContracts.CreateDocument("text/xml")) { uri ->
        uri?.let {
            try {
                val outputStream = requireContext().contentResolver.openOutputStream(it)
                val writer = OutputStreamWriter(outputStream)
                val currentList = viewModel.favorites.value ?: emptyList()
                writer.write(ExportHelper.convertToMALXml(currentList))
                writer.close()
                Toast.makeText(context, "Account exported successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStatLabels()
        setupImportExport()
        observeStats()
        observeImportStatus()
    }

    private fun observeImportStatus() {
        viewModel.importStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
        }
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

    private fun setupImportExport() {
        binding.importButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            if (username.isNotEmpty()) {
                Toast.makeText(context, "Importing list for $username...", Toast.LENGTH_SHORT).show()
                viewModel.importMalList(username)
                binding.usernameInput.text?.clear()
            } else {
                Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }

        binding.importXmlButton.setOnClickListener {
            xmlPicker.launch("text/xml")
        }

        binding.importDemoButton.setOnClickListener {
            try {
                // Look for any XML file in the assets folder to use as a demo
                val assetManager = requireContext().assets
                val files = assetManager.list("") ?: emptyArray()
                val xmlFile = files.find { it.endsWith(".xml") }
                
                if (xmlFile != null) {
                    val inputStream = assetManager.open(xmlFile)
                    val list = ImportHelper.parseMalXml(inputStream)
                    list.forEach { anime -> viewModel.updateAnimeInList(anime) }
                    Toast.makeText(context, "Loaded $xmlFile from assets!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No .xml file found in assets folder", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading demo data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.exportAccountButton.setOnClickListener {
            accountExporter.launch("WatchRyu_Account_Export.xml")
        }
    }

    private fun observeStats() {
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            // 1. Group by status for the Pie Chart
            val statusCounts = list.groupingBy { it.status }.eachCount()
            
            val entries = mutableListOf<PieEntry>()
            statusCounts.forEach { (status, count) ->
                if (status != AnimeStatus.AIRING && count > 0) {
                    entries.add(PieEntry(count.toFloat(), status.name.replace("_", " ")))
                }
            }

            // Get theme-aware color for text
            val typedValue = android.util.TypedValue()
            context?.theme?.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
            val textColor = typedValue.data

            val dataSet = PieDataSet(entries, "")
            dataSet.apply {
                colors = ColorTemplate.JOYFUL_COLORS.toList()
                valueTextColor = textColor
                valueTextSize = 12f
                sliceSpace = 3f
                // Move labels outside to fix overlapping
                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                valueLinePart1OffsetPercentage = 80f
                valueLinePart1Length = 0.3f
                valueLinePart2Length = 0.3f
                valueLineColor = textColor
            }

            binding.pieChart.apply {
                data = PieData(dataSet)
                centerText = "Library\nBreakdown"
                setCenterTextSize(14f)
                setCenterTextColor(textColor)
                setHoleColor(Color.TRANSPARENT)
                setTransparentCircleAlpha(0)
                // Add offsets to prevent labels from cutting off
                setExtraOffsets(30f, 0f, 30f, 0f)
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                setEntryLabelColor(textColor)
                setEntryLabelTextSize(11f)
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
