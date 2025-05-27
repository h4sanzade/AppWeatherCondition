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

class ConditionWeek : Fragment() {

    private lateinit var locationTextView: TextView
    private lateinit var weatherTextView: TextView
    private lateinit var weatherApiService: WeatherApiService


    private lateinit var tempToday: TextView
    private lateinit var tempMon: TextView
    private lateinit var tempTue: TextView
    private lateinit var tempWed: TextView
    private lateinit var tempThu: TextView
    private lateinit var tempFri: TextView
    private lateinit var tempSat: TextView
    private lateinit var tempSun: TextView
    private lateinit var tempSecondMon: TextView
    private lateinit var tempSecondTue: TextView

    private val API_KEY = "ba936bca0b46404a9fe122638252405"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_condition_week, container, false)


        locationTextView = view.findViewById(R.id.locationTextView)
        weatherTextView = view.findViewById(R.id.weatherTextView)
        tempToday = view.findViewById(R.id.tempToday)
        tempMon = view.findViewById(R.id.tempMon)
        tempTue = view.findViewById(R.id.tempTue)
        tempWed = view.findViewById(R.id.tempWed)
        tempThu = view.findViewById(R.id.tempThu)
        tempFri = view.findViewById(R.id.tempFri)
        tempSat = view.findViewById(R.id.tempSat)
        tempSun = view.findViewById(R.id.tempSun)
        tempSecondMon = view.findViewById(R.id.tempSecondMon)
        tempSecondTue = view.findViewById(R.id.tempSecondTue)

        weatherApiService = RetrofitClient.instance.create(WeatherApiService::class.java)

        fetchCurrentWeatherData("Baku")
        fetchForecastData("Baku")

        return view
    }

    private fun fetchCurrentWeatherData(location: String) {
        lifecycleScope.launch {
            try {
                val response = weatherApiService.getCurrentWeather(API_KEY, location)
                updateCurrentWeatherUI(response)
            } catch (e: Exception) {
                showError("Failed to get current weather data: ${e.message}")
            }
        }
    }

    private fun fetchForecastData(location: String) {
        lifecycleScope.launch {
            try {
                val response = weatherApiService.getForecastWeather(API_KEY, location, 10)
                updateForecastUI(response)
            } catch (e: Exception) {
                showError("Failed to get forecast data: ${e.message}")
            }
        }
    }

    private fun updateCurrentWeatherUI(weatherResponse: WeatherResponse) {
        val locationText = "${weatherResponse.location.name}, ${weatherResponse.location.country}"
        val weatherText = "${weatherResponse.current.temp_c.toInt()}°C\n${weatherResponse.current.condition.text}"

        locationTextView.text = locationText
        weatherTextView.text = weatherText
        tempToday.text = "${weatherResponse.current.temp_c.toInt()}°C"
    }

    private fun updateForecastUI(forecastResponse: ForecastResponse) {
        val tempViews = listOf(
            tempMon, tempTue, tempWed, tempThu,
            tempFri, tempSat, tempSun, tempSecondMon, tempSecondTue
        )


        forecastResponse.forecast.forecastday.drop(1).take(9).forEachIndexed { index, day ->
            if (index < tempViews.size) {
                val avgTemp = ((day.day.maxtemp_c + day.day.mintemp_c) / 2).toInt()
                tempViews[index].text = "${avgTemp}°C"
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        locationTextView.text = "Location Not Found"
        weatherTextView.text = "No Data"
    }
}