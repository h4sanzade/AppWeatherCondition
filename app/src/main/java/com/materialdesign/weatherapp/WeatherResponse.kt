package com.materialdesign.weatherapp

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather
)

data class Location(
    val name: String,
    val country: String
)

data class CurrentWeather(
    val temp_c: Double,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String
)