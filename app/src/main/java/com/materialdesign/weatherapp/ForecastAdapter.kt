package com.materialdesign.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            val avgTemp = ((forecastDay.day.maxtemp_c + forecastDay.day.mintemp_c) / 2).toInt()

            binding.textDay.text = dayName
            binding.textTemp.text = "${avgTemp}°C"
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
