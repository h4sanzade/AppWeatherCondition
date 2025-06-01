package com.materialdesign.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materialdesign.weatherapp.SearchSuggestion.SearchSuggestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApiService = RetrofitClient.instance.create(WeatherApiService::class.java)
    private val API_KEY = "ba936bca0b46404a9fe122638252405"

    private val _currentWeather = MutableLiveData<WeatherResponse>()
    val currentWeather: LiveData<WeatherResponse> = _currentWeather

    private val _forecastWeather = MutableLiveData<List<ForecastDay>>()
    val forecastWeather: LiveData<List<ForecastDay>> = _forecastWeather

    private val _searchSuggestions = MutableLiveData<List<SearchSuggestion>>()
    val searchSuggestions: LiveData<List<SearchSuggestion>> = _searchSuggestions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var searchJob: Job? = null

    fun fetchCurrentWeather(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            try {
                val response = weatherApiService.getCurrentWeather(API_KEY, location)
                _currentWeather.value = response
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
            _error.value = ""
            try {
                val response = weatherApiService.getForecastWeather(API_KEY, location, 10)
                _forecastWeather.value = response.forecast.forecastday.drop(1)
            } catch (e: Exception) {
                _error.value = "Failed to get forecast data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchLocations(query: String) {
        // Cancel previous search job
        searchJob?.cancel()

        if (query.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            try {
                // Add delay to avoid too many API calls
                delay(300)

                _isSearching.value = true
                val suggestions = weatherApiService.searchLocations(API_KEY, query)
                _searchSuggestions.value = suggestions
            } catch (e: Exception) {
                // Don't show error for search, just clear suggestions
                _searchSuggestions.value = emptyList()
                println("Search error: ${e.message}") // For debugging
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearchSuggestions() {
        _searchSuggestions.value = emptyList()
        searchJob?.cancel()
        _isSearching.value = false
    }
}