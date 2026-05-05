package com.example.mob_dev_portfolio.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.mob_dev_portfolio.databinding.DialogTrackAnimeBinding
import com.example.mob_dev_portfolio.model.Anime
import com.example.mob_dev_portfolio.model.AnimeStatus

/**
 * Dialog that allows users to edit their tracking info for a show.
 */
class TrackAnimeDialog(
    private val anime: Anime,
    private val onSave: (Anime) -> Unit
) : DialogFragment() {

    private var _binding: DialogTrackAnimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTrackAnimeBinding.inflate(layoutInflater)

        setupUI()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedStatus = AnimeStatus.entries[binding.statusSpinner.selectedItemPosition + 1] // +1 to skip AIRING
                val watched = binding.episodesWatchedInput.text.toString().toIntOrNull() ?: 0
                val score = binding.scoreSeekBar.progress

                onSave(anime.copy(
                    status = updatedStatus,
                    episodesWatched = watched,
                    score = score
                ))
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun setupUI() {
        binding.dialogTitle.text = anime.title
        binding.totalEpisodesLabel.text = " / ${anime.episodes}"
        binding.episodesWatchedInput.setText(anime.episodesWatched.toString())
        
        // Populate Spinner with statuses (excluding AIRING)
        val statuses = AnimeStatus.entries.filter { it != AnimeStatus.AIRING }.map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.statusSpinner.adapter = adapter
        
        // Select current status
        val currentPos = AnimeStatus.entries.filter { it != AnimeStatus.AIRING }.indexOf(anime.status)
        if (currentPos >= 0) binding.statusSpinner.setSelection(currentPos)

        // Setup Score Seekbar
        binding.scoreSeekBar.progress = anime.score
        binding.scoreValueLabel.text = anime.score.toString()
        binding.scoreSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.scoreValueLabel.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
