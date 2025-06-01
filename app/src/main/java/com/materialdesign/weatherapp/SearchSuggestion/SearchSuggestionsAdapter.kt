package com.materialdesign.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.materialdesign.weatherapp.SearchSuggestion.SearchSuggestion
import com.materialdesign.weatherapp.databinding.ItemSearchSuggestionBinding

class SearchSuggestionsAdapter(
    private val onSuggestionClick: (SearchSuggestion) -> Unit
) : ListAdapter<SearchSuggestion, SearchSuggestionsAdapter.SuggestionViewHolder>(SuggestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding = ItemSearchSuggestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SuggestionViewHolder(private val binding: ItemSearchSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(suggestion: SearchSuggestion) {
            binding.locationName.text = suggestion.name
            binding.locationDetails.text = if (suggestion.region.isNotEmpty() && suggestion.region != suggestion.name) {
                "${suggestion.region}, ${suggestion.country}"
            } else {
                suggestion.country
            }

            binding.root.setOnClickListener {
                onSuggestionClick(suggestion)
            }
        }
    }
}

class SuggestionDiffCallback : DiffUtil.ItemCallback<SearchSuggestion>() {
    override fun areItemsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem == newItem
    }
}