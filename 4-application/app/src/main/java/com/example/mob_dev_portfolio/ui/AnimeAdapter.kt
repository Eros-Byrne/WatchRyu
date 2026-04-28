package com.example.mob_dev_portfolio.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.databinding.ItemAnimeBinding
import com.example.mob_dev_portfolio.model.Anime

/**
 * Updated Adapter to handle progress tracking and multiple tabs.
 */
class AnimeAdapter(
    private val onTrackClick: (Anime) -> Unit
) : ListAdapter<Anime, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(getItem(position), onTrackClick)
    }

    class AnimeViewHolder(private val binding: ItemAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime, onTrackClick: (Anime) -> Unit) {
            binding.animeTitle.text = anime.title
            binding.animeSynopsis.text = anime.synopsis
            
            // Show episodes watched / total
            val context = binding.root.context
            binding.episodeInfo.text = context.getString(R.string.episode_progress, anime.episodesWatched, anime.episodes)
            binding.scoreInfo.text = context.getString(R.string.score_label, anime.score)

            binding.animeImage.load(anime.imageUrl) {
                crossfade(true)
            }

            // Expansion logic
            binding.detailSection.visibility = if (anime.isExpanded) View.VISIBLE else View.GONE
            binding.clickToExpand.text = if (anime.isExpanded) {
                context.getString(R.string.tap_to_collapse)
            } else {
                context.getString(R.string.tap_for_details)
            }

            binding.root.setOnClickListener {
                anime.isExpanded = !anime.isExpanded
                binding.detailSection.visibility = if (anime.isExpanded) View.VISIBLE else View.GONE
                binding.clickToExpand.text = if (anime.isExpanded) {
                    context.getString(R.string.tap_to_collapse)
                } else {
                    context.getString(R.string.tap_for_details)
                }
            }

            binding.wikiButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(anime.wikiUrl))
                binding.root.context.startActivity(intent)
            }

            // Button to open track dialog or perform quick add
            binding.addToListButton.setOnClickListener {
                onTrackClick(anime)
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean = oldItem == newItem
    }
}
