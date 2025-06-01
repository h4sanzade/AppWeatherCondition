package com.materialdesign.weatherapp.SearchSuggestion

import com.materialdesign.weatherapp.ForecastResponse
import com.materialdesign.weatherapp.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

data class SearchSuggestion(
    val id: Long,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
) {
    fun getDisplayText(): String {
        return if (region.isNotEmpty() && region != name) {
            "$name, $region, $country"
        } else {
            "$name, $country"
        }
    }
}

interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): WeatherResponse

    @GET("forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int
    ): ForecastResponse

    @GET("search.json")
    suspend fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<SearchSuggestion>
}