package com.materialdesign.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.materialdesign.weatherapp.databinding.FragmentConditionWeekBinding

class ConditionWeekFragment : Fragment() {

    private var _binding: FragmentConditionWeekBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var forecastAdapter: ForecastAdapter
    private var currentLocation: String = "Baku"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConditionWeekBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentLocation = arguments?.getString("location") ?: "Baku"

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchCurrentWeather(currentLocation)
        viewModel.fetchForecastWeather(currentLocation)
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.forecastRecyclerView.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { weatherResponse ->
            updateCurrentWeatherUI(weatherResponse)
        }

        viewModel.forecastWeather.observe(viewLifecycleOwner) { forecastList ->
            forecastAdapter.submitList(forecastList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                showError(error)
            }
        }
    }

    private fun updateCurrentWeatherUI(weatherResponse: WeatherResponse) {
        val locationText = "${weatherResponse.location.name}, ${weatherResponse.location.country}"
        val weatherText = "${weatherResponse.current.temp_c.toInt()}°C\n${weatherResponse.current.condition.text}"
        val todayTemp = "${weatherResponse.current.temp_c.toInt()}°C"

        binding.locationTextView.text = locationText
        binding.weatherTextView.text = weatherText
        binding.todayTemp.text = todayTemp

        loadWeatherIcon(weatherResponse.current.condition.icon)
    }

    private fun loadWeatherIcon(iconUrl: String) {
        val fullIconUrl = if (iconUrl.startsWith("//")) {
            "https:$iconUrl"
        } else {
            iconUrl
        }

        Glide.with(this)
            .load(fullIconUrl)
            .placeholder(R.drawable.ic_cloud_placeholder)
            .error(R.drawable.ic_cloud_placeholder)
            .into(binding.weatherIconImageView)
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        binding.locationTextView.text = "Location Not Found"
        binding.weatherTextView.text = "No Data"
        binding.todayTemp.text = "N/A"
        binding.weatherIconImageView.setImageDrawable(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}