package com.materialdesign.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.materialdesign.weatherapp.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : ListAdapter<ForecastDay, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ForecastViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastDay: ForecastDay) {
            val dayName = getDayName(forecastDay.date)
            val tempRange = "${forecastDay.day.maxtemp_c.toInt()}°/${forecastDay.day.mintemp_c.toInt()}°C"

            binding.textDay.text = dayName
            binding.textTemp.text = tempRange

            // Load weather icon
            loadWeatherIcon(forecastDay.day.condition.icon)
        }

        private fun loadWeatherIcon(iconUrl: String) {
            val fullIconUrl = if (iconUrl.startsWith("//")) {
                "https:$iconUrl"
            } else {
                iconUrl
            }

            Glide.with(binding.root.context)
                .load(fullIconUrl)
                .placeholder(R.drawable.ic_cloud_placeholder)
                .error(R.drawable.ic_cloud_placeholder)
                .into(binding.weatherIcon)
        }

        private fun getDayName(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}

class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastDay>() {
    override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
        return oldItem == newItem
    }
}