package com.materialdesign.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainScreen : Fragment() {

    private lateinit var locationTextView: TextView
    private lateinit var weatherTextView: TextView
    private lateinit var weatherApiService: WeatherApiService

    private val API_KEY = "ba936bca0b46404a9fe122638252405"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        locationTextView = view.findViewById(R.id.locationTextView)
        weatherTextView = view.findViewById(R.id.weatherTextView)

        weatherApiService = RetrofitClient.instance.create(WeatherApiService::class.java)

        fetchWeatherData("Istanbul")

        return view
    }

    private fun fetchWeatherData(location: String) {
        lifecycleScope.launch {
            try {
                val response = weatherApiService.getCurrentWeather(API_KEY, location)
                updateUI(response)
            } catch (e: Exception) {
                showError("Failed to get weather data: ${e.message}")
            }
        }
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        val locationText = "${weatherResponse.location.name}, ${weatherResponse.location.country}"
        val weatherText = "${weatherResponse.current.temp_c.toInt()}°C\n${weatherResponse.current.condition.text}"

        locationTextView.text = locationText
        weatherTextView.text = weatherText
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        locationTextView.text = "Location Not Found"
        weatherTextView.text = "No Data"
    }
}