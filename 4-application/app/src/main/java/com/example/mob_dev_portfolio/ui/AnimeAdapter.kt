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
import com.example.mob_dev_portfolio.databinding.ItemAnimeBinding
import com.example.mob_dev_portfolio.model.Anime

/**
 * Custom Adapter for the Anime list. 
 * I used ListAdapter because it handles list updates more efficiently than a standard RecyclerView adapter.
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
            
            // Loading the cover art using Coil
            binding.animeImage.load(anime.imageUrl) {
                crossfade(true)
            }

            // This part handles the "panel" expansion logic.
            // If the item is marked as expanded, we show the detail section.
            binding.detailSection.visibility = if (anime.isExpanded) View.VISIBLE else View.GONE
            binding.clickToExpand.text = if (anime.isExpanded) "Tap to collapse" else "Tap for more details..."

            // When the user clicks the card, we toggle the expansion state.
            binding.root.setOnClickListener {
                anime.isExpanded = !anime.isExpanded
                // We notify the adapter that this specific item has changed to trigger the UI update.
                binding.detailSection.visibility = if (anime.isExpanded) View.VISIBLE else View.GONE
                binding.clickToExpand.text = if (anime.isExpanded) "Tap to collapse" else "Tap for more details..."
            }

            // Link to the wiki using an Intent.
            binding.wikiButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(anime.wikiUrl))
                binding.root.context.startActivity(intent)
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            // Using ID for comparison as IDs are unique for each anime.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            // Data classes handle equals() automatically.
            return oldItem == newItem
        }
    }
}
