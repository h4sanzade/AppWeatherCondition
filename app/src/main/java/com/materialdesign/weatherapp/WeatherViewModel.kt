package com.materialdesign.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApiService = RetrofitClient.instance.create(WeatherApiService::class.java)
    private val API_KEY = "ba936bca0b46404a9fe122638252405"

    private val _currentWeather = MutableLiveData<WeatherResponse>()
    val currentWeather: LiveData<WeatherResponse> = _currentWeather

    private val _forecastWeather = MutableLiveData<List<ForecastDay>>()
    val forecastWeather: LiveData<List<ForecastDay>> = _forecastWeather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchCurrentWeather(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = weatherApiService.getCurrentWeather(API_KEY, location)
                _currentWeather.value = response
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to get weather data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchForecastWeather(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = weatherApiService.getForecastWeather(API_KEY, location, 10)
                _forecastWeather.value = response.forecast.forecastday.drop(1).take(9)
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to get forecast data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
