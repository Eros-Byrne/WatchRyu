package com.example.mob_dev_portfolio.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mob_dev_portfolio.databinding.ItemAnimeBinding
import com.example.mob_dev_portfolio.model.Anime

/**
 * Adapter for the Anime RecyclerView.
 * Uses ListAdapter and DiffUtil for optimized updates, as required by the assignment
 * to avoid the performance-heavy notifyDataSetChanged().
 */
class AnimeAdapter(
    private val onFavoriteClick: (Anime) -> Unit
) : ListAdapter<Anime, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnimeViewHolder(private val binding: ItemAnimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime) {
            binding.animeTitle.text = anime.title
            binding.animeSynopsis.text = anime.synopsis
            
            // Loading image using Coil (Modern library for Android)
            binding.animeImage.load(anime.imageUrl) {
                crossfade(true)
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }
}
