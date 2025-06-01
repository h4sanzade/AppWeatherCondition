package com.materialdesign.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materialdesign.weatherapp.SearchSuggestion.SearchSuggestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> _error.value = "Location not found. Please check the city name."
                    401 -> _error.value = "API key is invalid."
                    403 -> _error.value = "API key has exceeded calls per month quota."
                    else -> _error.value = "Failed to get weather data: ${e.message()}"
                }
            } catch (e: IOException) {
                _error.value = "Network error. Please check your internet connection."
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred: ${e.message}"
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
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> _error.value = "Location not found for forecast data."
                    401 -> _error.value = "API key is invalid."
                    403 -> _error.value = "API key has exceeded calls per month quota."
                    else -> _error.value = "Failed to get forecast data: ${e.message()}"
                }
            } catch (e: IOException) {
                _error.value = "Network error. Please check your internet connection."
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred while getting forecast: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchLocations(query: String) {

        searchJob?.cancel()

        if (query.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            try {

                delay(300)

                _isSearching.value = true
                val suggestions = weatherApiService.searchLocations(API_KEY, query)
                _searchSuggestions.value = suggestions.take(5)
            } catch (e: HttpException) {
                _searchSuggestions.value = emptyList()
                println("Search HTTP error: ${e.code()} - ${e.message()}")
            } catch (e: IOException) {
                _searchSuggestions.value = emptyList()
                println("Search network error: ${e.message}")
            } catch (e: Exception) {
                _searchSuggestions.value = emptyList()
                println("Search error: ${e.message}")
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

    fun clearError() {
        _error.value = ""
    }
}